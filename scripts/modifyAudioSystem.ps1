param([switch]$j, [switch]$a)

if ($j -AND $a) {
    Throw "Either -j or -a should be used, but not both"
}

$AudioSystemFile="C:\Users\Hector Mendez\Desktop\gervill-control\scripts\AudioSystem.java"

if ($j) {
	$target="src"
} elseif ($a) {
	$target="app\src\main\java"
} else {
	Throw "Missing flag"
}

Copy-Item $AudioSystemFile $target\gervill\javax\sound\sampled\AudioSystem.java
