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
		var filterGrep, filterFindstr, commandWithFilterText;

		executablePath = executablePath ?? { lilypondPath };
		lilypondBase = path.splitext[0];
		outputPath = outputPath ? lilypondBase;

		flags = ((flags ? "") ++ "%").format("-dno-point-and-click -o");

		filterGrep = "2>&1 | grep -vE " ++
		"'^(Processing|Parsing\\.\\.\\.|Interpreting music\\.\\.\\.|" ++
		"Preprocessing graphical objects\\.\\.\\.|" ++
		"Finding the ideal number of pages\\.\\.\\.|" ++
		"Fitting music on [0-9]+ page[s]?\\.\\.\\.|Drawing systems\\.\\.\\.|" ++
		"Converting to .+)$' | " ++
		"grep -vE '^$'";

		filterFindstr = "2>&1" +
		"| findstr /V /R /C:\"^Processing$\"" +
		"| findstr /V /R /C:\"^Parsing\\.\\.\\.$\"" +
		"| findstr /V /R /C:\"^Interpreting music\\.\\.\\.$\"" +
		"| findstr /V /R /C:\"^Preprocessing graphical objects\\.\\.\\.$\"" +
		"| findstr /V /R /C:\"^Finding the ideal number of pages\\.\\.\\.$\"" +
		"| findstr /V /R /C:\"^Fitting music on [0-9][0-9]* page[s]*\\.\\.\\.$\"" +
		"| findstr /V /R /C:\"^Drawing systems\\.\\.\\.$\"" +
		"| findstr /V /R /C:\"^Converting to .*$\"" +
		"| findstr /V /R /C:\"^$\"";

		command = "% % % %".format(
			Fosc.crossPlatformPath(executablePath),
			flags,
			Fosc.crossPlatformPath(outputPath),
			Fosc.crossPlatformPath(path)
		);

		commandWithFilterText = Platform.case(
			\linux,   { command + filterGrep },
			\osx,     { if(Fosc.lilypondPath.contains("homebrew")) {
					"zsh -lc" + (command + filterGrep).shellQuote
				}{
					command + filterGrep
				}
			},
			\windows, { Fosc.crossPlatformPath(command + filterFindstr) }
		);

		exitCode = systemCmd(commandWithFilterText);
		success = (exitCode == 0);
		if (success && clean) { File.delete(path) };

		^success;
	}
}
