## Steps to make this run:

1. Add the sources under `/src` to your IDE (IntelliJ IDEA or Eclipse).
2. Delete the `src\gervill\com\sun\media\sound\JARSoundbankReader.java` file.
3. Delete the `getSourceDataLine(AudioFormat format, Mixer.Info mixerinfo)` method of `src\gervill\javax\sound\sampled\AudioSystem.java`.
4. Add the import `import impl.SourceDataLineImpl;` to `src\gervill\javax\sound\sampled\AudioSystem.java`.
5. Change the body of the `getSourceDataLine(AudioFormat format)` method of `src\gervill\javax\sound\sampled\AudioSystem.java` such that it only returns a `new SourceDataLineImpl(format);` (this will be the only line of the method).