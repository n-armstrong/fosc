/* ------------------------------------------------------------------------------------------------------------
• FoscIOManager

Manages Fosc IO.
------------------------------------------------------------------------------------------------------------ */
FoscIOManager : Fosc {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • *deleteFile
    -------------------------------------------------------------------------------------------------------- */
    *deleteFile { |path|
        var returnCode;
        
        if (File.exists(path).not) {
            ^throw("%:%: path does not exist: %.".format(this.name, thisMethod.name, path));
        };
        
        path = shellQuote(path);
        returnCode = systemCmd("rm %".format(path));
        
        ^returnCode;
    }
    /* --------------------------------------------------------------------------------------------------------
    • *lastOutputFileName

    Gets last output file name in 'outputDirectory'.

    Gets last output file name in Fosc output directory when 'outputDirectory' is nil. Returns nil when output directory contains no output files.

    Returns string or nil.


    • Example 1

    d = Fosc.outputDirectory;
    FoscIOManager.lastOutputFileName("ly", d);

    FoscIOManager.lastOutputFileName;
    -------------------------------------------------------------------------------------------------------- */
    *lastOutputFileName { |extension, outputDirectory|
        var pattern, allFileNames, allOutput, result;
        
        pattern = "\\d{4,4}.[a-zA-Z]{2,3}";
        outputDirectory = outputDirectory ?? { Fosc.outputDirectory };
        if (File.exists(outputDirectory).not) { ^nil };
        allFileNames = "%/*".format(outputDirectory).pathMatch.collect { |each| each.basename };
        
        if (extension.notNil) {
            allOutput = allFileNames.select { |each|
                pattern.matchRegexp(each) && { each.splitext[1] == extension };
            };
        } {
            allOutput = allFileNames.select { |each| pattern.matchRegexp(each) };
        };
        
        result = if (allOutput.isEmpty) { nil } { allOutput.sort.last };
        
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • *moveFile
    -------------------------------------------------------------------------------------------------------- */
    *moveFile { |sourcePath, destinationPath|
        var returnCode;
        
        if (File.exists(sourcePath).not) {
            ^throw("%:%: sourcePath does not exist: %.".format(this.name, thisMethod.name, sourcePath));
        };
        
        sourcePath = shellQuote(sourcePath);
        destinationPath = shellQuote(destinationPath);
        returnCode = systemCmd("mv % %".format(sourcePath, destinationPath));
        
        ^returnCode;
    }
    /* --------------------------------------------------------------------------------------------------------
    • *nextOutputFileName

    Gets next output file name with 'extension' 'outputDirectory'.

    Returns string.
    -------------------------------------------------------------------------------------------------------- */
    *nextOutputFileName { |extension='ly', outputDirectory|
        var lastOutput, nextNumber, lastNumber, result;

        lastOutput = FoscIOManager.lastOutputFileName(outputDirectory: outputDirectory);
        
        if (lastOutput.isNil) {
            nextNumber = 1;
            result = "0001.%".format(extension);
        } {
            lastNumber = lastOutput.splitext[0].interpret;
            nextNumber = lastNumber + 1;
            result = "%.%".format(nextNumber.asDigits(10, 4).join, extension);
        };
        
        if (nextNumber > 9000) {
            warn("%: output Directory is almost full: %.".format(this.species, outputDirectory));
        };
        
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • *openFile
    -------------------------------------------------------------------------------------------------------- */
    *openFile { |path|
        if (File.exists(path).not) {
            ^throw("%:%: path does not exist: %.".format(this.name, thisMethod.name, path));
        };
        
        openOS(path);
    }
    /* --------------------------------------------------------------------------------------------------------
    • *runLilypond

    a = FoscNote(60, 1/4);
    b = a.write.asPDF(clean: true);
    openOS(b);

    systemCmd("/opt/local/bin/lilypond \"/Users/newton/Library/Application Support/SuperCollider/fosc-output/0001.ly\"");

    Fosc.lilypondVersion;

    a = FoscNote(60, 1/4);
    a.show;

    m = "/opt/local/bin/lilypond  -dno-point-and-click -o '/Users/newton/Library/Application Support/SuperCollider/fosc-output/0001' '/Users/newton/Library/Application Support/SuperCollider/fosc-output/0001.ly'";

    runInTerminal(m);

    unixCmd(m);

    systemCmd("ls -l /opt/local/bin");

    FoscIOManager.runLilypond("%/0001.ly".format(Fosc.outputDirectory));
    -------------------------------------------------------------------------------------------------------- */
    *runLilypond { |path, flags, outputPath, executablePath, clean=false|
        var lilypondBase, command, exitCode, success;
        
        executablePath = executablePath ?? { Fosc.lilypondPath };
        lilypondBase = path.splitext[0];
        outputPath = outputPath ? lilypondBase;
        flags = ((flags ? "") ++ " %").format("-dno-point-and-click -o");
        command = "% % % %".format(executablePath, flags, outputPath.shellQuote, path.shellQuote);
        exitCode = systemCmd(command);
        success = (exitCode == 0);
        if (success && clean) { File.delete(path) };
        
        ^success;
    }
}
