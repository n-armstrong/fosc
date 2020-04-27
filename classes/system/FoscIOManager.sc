/* ------------------------------------------------------------------------------------------------------------
• FoscIOManager

Manages Fosc IO.
------------------------------------------------------------------------------------------------------------ */
FoscIOManager : FoscObject {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • *deleteFile
    -------------------------------------------------------------------------------------------------------- */
    *deleteFile { |path|
        var returnCode;
        if (File.exists(path).not) {
            throw("%:%: path does not exist: %.".format(this.name, thisMethod.name, path));
        };
        path = shellQuote(path);
        returnCode = systemCmd("rm %".format(path));
        ^returnCode;
    }
    /* --------------------------------------------------------------------------------------------------------
    • *lastOutputFileName

    Gets last output file name in output_directory.

    Gets last output file name in Abjad output directory when output_directory is none. Returns nil when output directory contains no output files.

    Returns string or nil.

    d = FoscConfiguration.foscOutputDirectory;
    FoscIOManager.lastOutputFileName("ly", d);

    FoscIOManager.lastOutputFileName;
    -------------------------------------------------------------------------------------------------------- */
    *lastOutputFileName { |extension, outputDirectory|
        var pattern, allFileNames, allOutput, result;
        pattern = "\\d{4,4}.[a-z]{2,3}";
        outputDirectory = outputDirectory ?? { FoscConfiguration.foscOutputDirectory };
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

    !!!TODO: TEST WHETHER SHELL QUOTE NEEDS TO BE ADDED

    a = "/Users/newton/Library/Application Support/blah";
    a.beginsWith("'") && a.endsWith("'");
    a = a.shellQuote;
    a.beginsWith("'") && a.endsWith("'");
    -------------------------------------------------------------------------------------------------------- */
    *moveFile { |sourcePath, destinationPath|
        var returnCode;
        if (File.exists(sourcePath).not) {
            throw("%:%: sourcePath does not exist: %.".format(this.name, thisMethod.name, sourcePath));
        };
        sourcePath = shellQuote(sourcePath);
        destinationPath = shellQuote(destinationPath);
        returnCode = systemCmd("mv % %".format(sourcePath, destinationPath));
        ^returnCode;
    }
    /* --------------------------------------------------------------------------------------------------------
    • *nextOutputFileName

    Gets next output file name with file_extension in output_directory.

    Returns string.


    d = FoscConfiguration.foscOutputDirectory;
    FoscIOManager.nextOutputFileName("ly", d);

    FoscIOManager.nextOutputFileName;
    -------------------------------------------------------------------------------------------------------- */
    *nextOutputFileName { |extension='ly', outputDirectory|
        var lastOutput, nextNumber, lastNumber, result;
        // assert file_extension.isalpha() and \
        //     0 < len(file_extension) < 4, repr(file_extension)
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

    Opens 'file_path'.

    Uses 'application' when 'application' is not none.

    Uses Abjad configuration file 'text_editor' when 'application' is none.

    Takes best guess at operating system-specific file opener when both 'application' and Abjad configuration file 'text_editor' are none.

    Respects 'line_number' when 'file_path' can be opened with text editor.

    Returns none.



    a = "/Users/newton/Library/Application Support/blah";
    a.beginsWith("'") && a.endsWith("'");
    a = a.shellQuote;
    a.beginsWith("'") && a.endsWith("'");

    m = FoscLeafMaker().([60], [1/4]);
    m.show;

    START "" "path to my file"
    -------------------------------------------------------------------------------------------------------- */
    *openFile { |path, application|
        //!!!TODO: incomplete
        var returnCode;
        if (File.exists(path).not) {
            throw("%:%: path does not exist: %.".format(this.name, thisMethod.name, path));
        };
        path = shellQuote(path);
        //!!!TODO:
        returnCode = systemCmd("open %".format(path));
        returnCode = Platform.case(
            \osx,       { systemCmd("open %".format(path)) },
            \linux,     { systemCmd("xdg-open %".format(path)) },
            \windows,   { systemCmd("START \"\" %".format(path)) }
        );
        ^returnCode;
    }
    /* --------------------------------------------------------------------------------------------------------
    • *runLilypond

    a = FoscNote(60, 1/4);
    a.show;

    FoscPersistenceManager

    FoscIOManager.runLilypond("/Users/newton/Desktop/test.ly", outputPath: "/Users/newton/Desktop/foobar");

    FoscIOManager.runLilypond("/Users/newton/Desktop/test.ly", flags: "--png -dresolution=300", outputPath: "/Users/newton/Desktop/foobar");

    !!!TODO: TEST WHETHER SHELL QUOTE NEEDS TO BE ADDED

    a = "/Users/newton/Library/Application Support/blah";
    a.beginsWith("'") && a.endsWith("'");
    a = a.shellQuote;
    a.beginsWith("'") && a.endsWith("'");

	systemCmd("ls");

    FoscConfiguration.lilypondExecutablePath;

    systemCmd("% /Users/newton/Desktop/test.ly".format(FoscConfiguration.lilypondExecutablePath));
    unixCmdGetStdOut("% /Users/newton/Desktop/test.ly".format(FoscConfiguration.lilypondExecutablePath));
    -------------------------------------------------------------------------------------------------------- */
    *runLilypond { |lyPath, flags, outputPath, executablePath|
        //!!!TODO: incomplete
        var lilypondBase, command, exitCode, success;
        executablePath = executablePath ?? { FoscConfiguration.lilypondExecutablePath };
        lilypondBase = lyPath.splitext[0].shellQuote;
        lyPath = lyPath.shellQuote;
        flags = flags ? "";
        outputPath = outputPath ? lilypondBase;
        command = "% % -dno-point-and-click -o % %".format(executablePath, flags, outputPath, lyPath);
        exitCode = systemCmd(command);
        success = (exitCode == 0);
        ^success;
    }
}
