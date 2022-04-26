/* ------------------------------------------------------------------------------------------------------------
• FoscWriteManager
------------------------------------------------------------------------------------------------------------ */
FoscWriteManager : Fosc {
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

    Writes client as LilyPond file.

    Autogenerates file path when 'path' is nil.

    Returns output path.


    • Example 1

    a = FoscNote(60, 1/4);
    a.show(staffSize: 14);
    openOS(b);
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

    Writes client as PDF file.

    Autogenerates file path when 'path' is nil.

    Returns output path.
    

    • Example 1

    a = FoscNote(60, 1/4);
    b = a.write.asPDF;
    openOS(b);
    -------------------------------------------------------------------------------------------------------- */
    asPDF { |lyPath, outputPath, illustrateEnvir, flags|     
        if (illustrateEnvir.isNil) { assert(client.respondsTo('illustrate')) };
        lyPath = this.asLY(lyPath, illustrateEnvir);
        outputPath = outputPath ?? { lyPath.splitext[0] };
        FoscIOManager.runLilypond(lyPath, flags, outputPath);
        //if (clean) { File.delete(lyPath) };
        
        ^(outputPath ++ ".pdf");
    }
    /* --------------------------------------------------------------------------------------------------------
    • asPNG
    
    Writes client as cropped PNG file.

    Autogenerates file path when 'path' is nil.

    Returns output path.


    • Example 1

    a = FoscNote(60, 1/4);
    b = a.write.asPNG(resolution: 300);
    openOS(b);
    -------------------------------------------------------------------------------------------------------- */
    asPNG { |lyPath, outputPath, illustrateEnvir, resolution=300|
        var flags, files;
        
        if (illustrateEnvir.isNil) { assert(client.respondsTo('illustrate')) };
        lyPath = this.asLY(lyPath, illustrateEnvir);
        outputPath = outputPath ?? { lyPath.splitext[0] };
        flags = "-dbackend=eps -dresolution=% -dno-gs-load-fonts -dinclude-eps-fonts --png";
        flags = flags.format(resolution);
        FoscIOManager.runLilypond(lyPath, flags, outputPath);
        files = #["%-1.eps", "%-systems.count", "%-systems.tex", "%-systems.texi"];
        files.do { |each| File.delete(each.format(outputPath)) };
        
        ^(outputPath ++ ".png");
    }
    /* --------------------------------------------------------------------------------------------------------
    • asSVG
    
    Writes client as SVG file.

    Autogenerates file path when 'path' is nil.

    Returns output path.


    • Example 1

    a = FoscNote(60, 1/4);
    b = a.write.asSVG;
    openOS(b);
    -------------------------------------------------------------------------------------------------------- */
    asSVG { |lyPath, outputPath, illustrateEnvir|        
        if (illustrateEnvir.isNil) { assert(client.respondsTo('illustrate')) };
        lyPath = this.asLY(lyPath, illustrateEnvir);
        outputPath = outputPath ?? { lyPath.splitext[0] };
        FoscIOManager.runLilypond(lyPath, "-dbackend=svg", outputPath);

        ^(outputPath ++ ".svg");
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
