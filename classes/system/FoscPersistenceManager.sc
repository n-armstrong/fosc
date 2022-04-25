/* ------------------------------------------------------------------------------------------------------------
• FoscPersistenceManager
------------------------------------------------------------------------------------------------------------ */
FoscPersistenceManager : Fosc {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <client;
    *new { |client|
        ^super.new.init(client);
    }
    init { |argClient|
        client = argClient;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • asLY

    Persists client as LilyPond file.

    Autogenerates file path when 'path' is nil.

    Returns output path and elapsed formatting time when LilyPond output is written.


    a = FoscNote(60, 1/4);
    a.show(staffSize: 14);
    -------------------------------------------------------------------------------------------------------- */
    asLY { |path, illustrateEnvir|
        var illustration, lyFileName, lyFile;

        if (illustrateEnvir.isNil) {
            illustration = client.illustrate.lilypond;
        } {
            illustration = client.performWithEnvir('illustrate', illustrateEnvir).lilypond;
        };
        
        if (path.isNil) {
            lyFileName = FoscIOManager.nextOutputFileName;
            path = "%/%".format(Fosc.outputDirectory, lyFileName);
        };
        
        lyFile = File(path, "w");
        lyFile.write(illustration);
        lyFile.close;
        
        ^path;
    }
    /* --------------------------------------------------------------------------------------------------------
    • asMIDI
    -------------------------------------------------------------------------------------------------------- */
    asMIDI {
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • asPDF

    Persists client as PDF.
    
    Autogenerates file path when 'lyPath' is nil.

    Returns output path.

    If 'clean' is true, ly file is deleted.
    

    a = FoscNote(60, 1/4);
    b = a.write.asPDF;
    -------------------------------------------------------------------------------------------------------- */
    asPDF { |lyPath, outputPath, illustrateEnvir, flags, clean=false|
        var success;
        
        if (illustrateEnvir.isNil) { assert(client.respondsTo('illustrate')) };
        lyPath = this.asLY(lyPath, illustrateEnvir);
        outputPath = outputPath ?? { lyPath.splitext[0] };
        success = FoscIOManager.runLilypond(lyPath, flags, outputPath.shellQuote);
        if (success && clean) { File.delete(lyPath) };
        
        ^(outputPath ++ ".pdf");
    }
    /* --------------------------------------------------------------------------------------------------------
    • asPNG
    

    • Example 1

    a = FoscNote(60, 1/4);
    b = a.write.asPNG(resolution: 300);
    unixCmd("open " ++ b[0]);
    -------------------------------------------------------------------------------------------------------- */
    asPNG { |lyPath, outputPath, illustrateEnvir, resolution=300, clean=true|
        var flags, success;
        
        if (illustrateEnvir.isNil) { assert(client.respondsTo('illustrate')) };
        lyPath = this.asLY(lyPath, illustrateEnvir);
        outputPath = outputPath ?? { lyPath.splitext[0] };
        flags = "-dbackend=eps -dresolution=% -dno-gs-load-fonts -dinclude-eps-fonts --png".format(resolution);
        success = FoscIOManager.runLilypond(lyPath, flags, outputPath.shellQuote);
        
        if (success && clean) {
            #[
                "%-1.eps",
                "%-systems.count",
                "%-systems.tex",
                "%-systems.texi",
                "%.ly"
            ].do { |each| File.delete(each.format(outputPath)) };
        };
        
        ^(outputPath ++ ".png");
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • client

    Gets client of persistence manager.

    Returns component or selection.
    -------------------------------------------------------------------------------------------------------- */
}
