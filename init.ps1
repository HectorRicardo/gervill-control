param([switch]$j, [switch]$a)

if ($j -AND $a) {
    Throw "Either -j or -a should be used, but not both"
}

if ($j) {
	$basePath="src"
	$deleteShortcut="delete-classes-java.lnk"
	cmd.exe /c gi java,intellij,windows > .gitignore
} elseif ($a) {
	$basePath="app\src\main\java"
	$deleteShortcut="delete-classes-android.lnk"
	cmd.exe /c gi android,AndroidStudio,windows > .gitignore
} else {
	Throw "Missing flag"
}

$sourceRoot = "C:\Users\Hector Mendez\Desktop\gervill-control\output"

function reset {
	param([string]$sourcePath, [string]$destinationPath)
	
	$source="${sourceRoot}\${sourcePath}"
	$destination=".\${basePath}\${destinationPath}"
	
	if (Test-Path $destination) {
		Remove-Item $destination -Recurse
	}
	Copy-Item -Path $source -Recurse -Destination $destination -Container
}

reset -sourcePath src\gervill\ -destinationPath gervill\
reset -sourcePath src\gervilljava\ -destinationPath gervilljava\
reset -sourcePath src\samplesynth\ -destinationPath samplesynth\
reset -sourcePath assets\ -destinationPath ..\assets\

Copy-Item "${sourceRoot}\${deleteShortcut}" $deleteShortcut
