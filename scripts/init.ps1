param([switch]$j, [switch]$a)

#For PowerShell v3
Function gig {
  param(
    [Parameter(Mandatory=$true)]
    [string[]]$list
  )
  $params = ($list | ForEach-Object { [uri]::EscapeDataString($_) }) -join ","
  Invoke-WebRequest -Uri "https://www.toptal.com/developers/gitignore/api/$params" | select -ExpandProperty content | Out-File -FilePath $(Join-Path -path $pwd -ChildPath ".gitignore") -Encoding ascii
}

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
	$modifyAudioSystemShortcut="modifyAudioSystem-java.lnk"
	$mainAppFolder="gervilljava"
	gig java,eclipse,windows
} elseif ($a) {
	$basePath="app\src\main\java"
	$modifyAudioSystemShortcut="modifyAudioSystem-android.lnk"
	$mainAppFolder="gervillandroid"
	gig android,AndroidStudio,windows
	
	reset -sourcePath layout\ -destinationPath ..\res\layout\
} else {
	Throw "Missing flag"
}

Read-Host -Prompt "Press Enter to continue"

reset -sourcePath src\gervill\ -destinationPath gervill\
reset -sourcePath src\$mainAppFolder\ -destinationPath own\
reset -sourcePath src\samplesynth\ -destinationPath samplesynth\
reset -sourcePath assets\ -destinationPath ..\assets\

Copy-Item $sourceRoot\shortcuts\$modifyAudioSystemShortcut $modifyAudioSystemShortcut
