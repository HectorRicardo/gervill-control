param([switch]$j, [switch]$a)

if ($j -AND $a) {
    Throw "Either -j or -a should be used, but not both"
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

if ($j) {
	$basePath="src"
	$deleteShortcut="delete-classes-java.lnk"
	$modifyAudioSystemShortcut="modifyAudioSystem-java.lnk"
	$mainAppFolder="gervilljava"
	cmd.exe /c gi java,intellij+all,windows > .gitignore
} elseif ($a) {
	$basePath="app\src\main\java"
	$deleteShortcut="delete-classes-android.lnk"
	$modifyAudioSystemShortcut="modifyAudioSystem-android.lnk"
	$mainAppFolder="gervillandroid"
	cmd.exe /c gi android,AndroidStudio,windows > .gitignore
	
	reset -sourcePath layout\ -destinationPath ..\res\layout\
} else {
	Throw "Missing flag"
}

Read-Host -Prompt "Press Enter to continue"

reset -sourcePath src\gervill\ -destinationPath gervill\
reset -sourcePath src\$mainAppFolder\ -destinationPath own\
reset -sourcePath src\samplesynth\ -destinationPath samplesynth\
reset -sourcePath assets\ -destinationPath ..\assets\

Copy-Item $sourceRoot\shortcuts\$deleteShortcut $deleteShortcut
Copy-Item $sourceRoot\shortcuts\$modifyAudioSystemShortcut $modifyAudioSystemShortcut
