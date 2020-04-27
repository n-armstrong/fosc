/* ------------------------------------------------------------------------------------------------------------
• FoscPersistenceManager
------------------------------------------------------------------------------------------------------------ */
FoscPersistenceManager : FoscObject {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <client;
    var pngPagePattern=".+page(\\d+)\.png";
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

    !!!TODO: update to abjad 3.0
    -------------------------------------------------------------------------------------------------------- */
    asLY { |path, illustrateFunction ... args|
        var illustration, lyFileName, lyFile;
        if (illustrateFunction.isNil) {
            illustrateFunction = client.illustrate;
        };
        illustration = illustrateFunction.format; //!!!TODO: temporary
        if (path.isNil) {
            lyFileName = FoscIOManager.nextOutputFileName;
            path = FoscConfiguration.foscOutputDirectory ++ "/" ++ lyFileName;
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


    a = FoscNote(60, 1/4);
    a.show;


    a = FoscNote(60, 1/4);
    b = a.write.asPDF;
    -------------------------------------------------------------------------------------------------------- */
    asPDF { |lyPath, outputPath, illustrateFunction, removeLY=false ... args|
        var result, success;
        if (illustrateFunction.isNil) { assert(client.respondsTo('illustrate')) };
        lyPath = this.asLY(lyPath, illustrateFunction, *args);
        outputPath = outputPath ?? { lyPath.splitext[0] };
        success = FoscIOManager.runLilypond(lyPath, outputPath: outputPath.shellQuote);
        if (removeLY) { File.delete(lyPath) };
        outputPath = outputPath ++ ".pdf";
        ^[outputPath, success];
    }
    // asPDF { |path, illustrateFunction, removeLY=false ... args|
    //     var result, lyPath, pdfPath, success;
    //     if (illustrateFunction.isNil) { assert(client.respondsTo('illustrate')) };
    //     if (path.notNil) { lyPath = path.splitext[0] ++ ".ly" };
    //     result = this.asLY(lyPath, illustrateFunction, *args);
    //     lyPath = result;
    //     pdfPath = (lyPath.splitext[0] ++ ".pdf");
    //     success = FoscIOManager.runLilypond(lyPath);
    //     if (removeLY) { File.delete(lyPath) };
    //     ^[pdfPath, success];
    // }
    /* --------------------------------------------------------------------------------------------------------
    • asPNG
    -------------------------------------------------------------------------------------------------------- */
    asPNG {
        ^this.notYetImplemented(thisMethod);
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
