<?php

function getDotDiagramFromPost()
{
    return $_POST['dot_diagram'];
}

function createTempFile($content)
{
    $tempFile = tempnam(sys_get_temp_dir(), 'dot');
    file_put_contents($tempFile, $content);
    return $tempFile;
}

function convertToImage($tempFile)
{
    $command = "dot -Tpng $tempFile -o $tempFile.png";
    exec($command);
    return "$tempFile.png";
}

function getImageData($filePath)
{
    return file_get_contents($filePath);
}

function deleteTempFiles($tempFile, $imageFile)
{
    unlink($tempFile);
    unlink($imageFile);
}

$dotDiagram = getDotDiagramFromPost();
$tempFile = createTempFile($dotDiagram);
$imageFile = convertToImage($tempFile);
$imageData = getImageData($imageFile);

header('Content-Type: image/png');
echo $imageData;

deleteTempFiles($tempFile, $imageFile);
