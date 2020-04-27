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

    Gets last output file name in 'outputDirectory'.

    Gets last output file name in Fosc output directory when 'outputDirectory' is nil. Returns nil when output directory contains no output files.

    Returns string or nil.


    • Example 1

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

    Gets next output file name with 'extension' 'outputDirectory'.

    Returns string.
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
    -------------------------------------------------------------------------------------------------------- */
    *openFile { |path, application|
        var returnCode;
        if (File.exists(path).not) {
            throw("%:%: path does not exist: %.".format(this.name, thisMethod.name, path));
        };
        path = shellQuote(path);
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

    f = { |music, name, show=false|
        p = "%/images/%".format(FoscConfiguration.foscRootDirectory, name);
        x = music.write.asPDF("%.ly".format(p), p, flags: "-dresolution 100");
        //if (show) { unixCmd("open %".format(x[0])) };
    };


    a = FoscRhythmMaker();
    a.(divisions: [2/16,3/16,5/16], ratios: #[[3,1],[3,2],[4,3]]);
    a.show;

    f.(a, "rhythm-maker-1", show: true);
    -------------------------------------------------------------------------------------------------------- */
    *runLilypond { |lyPath, flags, outputPath, executablePath|
        var lilypondBase, command, exitCode, success;
        executablePath = executablePath ?? { FoscConfiguration.lilypondExecutablePath };
        lilypondBase = lyPath.splitext[0].shellQuote;
        lyPath = lyPath.shellQuote;
        flags = flags ? "";
        outputPath = outputPath ? lilypondBase;
        command = "% % -dno-point-and-click -o % %".format(executablePath, flags, outputPath, lyPath);
        "command: ".post; command.postln;
        exitCode = systemCmd(command);
        success = (exitCode == 0);
        ^success;
    }
}
