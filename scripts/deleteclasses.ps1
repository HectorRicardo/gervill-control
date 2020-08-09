param([switch]$j=$false, [switch]$a=$false)

if ($j -AND $a) {
    Throw "Either -j or -a should be used, but not both"
}

if ($j) {
	$basePath="src"
} elseif ($a) {
	$basePath="app\src\main\java"
} else {
	Throw "Missing flag"
}

function delete {
	param([string]$path)
	
	$finalPath="${basePath}\${path}"
	
	if (Test-Path $finalPath) {
		Remove-Item $finalPath -Recurse
	}
}

delete -path gervill\com\sun\media\sound\AiffFileFormat.java
delete -path gervill\com\sun\media\sound\AiffFileReader.java
delete -path gervill\com\sun\media\sound\AiffFileWriter.java
delete -path gervill\com\sun\media\sound\AlawCodec.java
delete -path gervill\com\sun\media\sound\AuFileFormat.java
delete -path gervill\com\sun\media\sound\AuFileReader.java
delete -path gervill\com\sun\media\sound\AuFileWriter.java
delete -path gervill\com\sun\media\sound\AudioFileSoundbankReader.java
delete -path gervill\com\sun\media\sound\AudioFloatFormatConverter.java
delete -path gervill\com\sun\media\sound\DLSSoundbankReader.java
delete -path gervill\com\sun\media\sound\DataPusher.java
delete -path gervill\com\sun\media\sound\JARSoundbankReader.java
delete -path gervill\com\sun\media\sound\JavaSoundAudioClip.java
delete -path gervill\com\sun\media\sound\MidiOutDeviceProvider.java
delete -path gervill\com\sun\media\sound\ModelAbstractChannelMixer.java
delete -path gervill\com\sun\media\sound\ModelAbstractOscillator.java
delete -path gervill\com\sun\media\sound\ModelStandardDirector.java
delete -path gervill\com\sun\media\sound\PCMtoPCMCodec.java
delete -path gervill\com\sun\media\sound\RealTimeSequencer.java
delete -path gervill\com\sun\media\sound\RealTimeSequencerProvider.java
delete -path gervill\com\sun\media\sound\SF2SoundbankReader.java
delete -path gervill\com\sun\media\sound\SimpleInstrument.java
delete -path gervill\com\sun\media\sound\SimpleSoundbank.java
delete -path gervill\com\sun\media\sound\SoftMidiAudioFileReader.java
delete -path gervill\com\sun\media\sound\SoftProvider.java
delete -path gervill\com\sun\media\sound\SoftShortMessage.java
delete -path gervill\com\sun\media\sound\StandardMidiFileReader.java
delete -path gervill\com\sun\media\sound\StandardMidiFileWriter.java
delete -path gervill\com\sun\media\sound\SunCodec.java
delete -path gervill\com\sun\media\sound\SunFileReader.java
delete -path gervill\com\sun\media\sound\SunFileWriter.java
delete -path gervill\com\sun\media\sound\UlawCodec.java
delete -path gervill\com\sun\media\sound\WaveExtensibleFileReader.java
delete -path gervill\com\sun\media\sound\WaveFileFormat.java
delete -path gervill\com\sun\media\sound\WaveFileReader.java
delete -path gervill\com\sun\media\sound\WaveFileWriter.java
delete -path gervill\com\sun\media\sound\WaveFloatFileReader.java
delete -path gervill\com\sun\media\sound\WaveFloatFileWriter.java
delete -path gervill\java\applet\AudioClip.java
delete -path gervill\javax\sound\sampled\EnumControl.java
delete -path gervill\javax\sound\sampled\ReverbType.java

delete -path gervill\java\
