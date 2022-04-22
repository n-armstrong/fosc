/* ------------------------------------------------------------------------------------------------------------
• Fosc

!!!TODO: Deprecate. All methods have been moved to Fosc.
------------------------------------------------------------------------------------------------------------ */
FoscConfiguration {
    classvar lilypondExecutablePath="lilypond", <tuning;
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • *initClass
    -------------------------------------------------------------------------------------------------------- */
    *initClass {
        var stylesheetsDir, returnCode;

        if (File.exists(this.outputDirectory).not) {
            File.mkdir(this.outputDirectory);
        };
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • *lilypondVersionString
    
	Fosc.lilypondVersionString;
    -------------------------------------------------------------------------------------------------------- */
    *lilypondVersionString {
    	var executablePath, str, versionString;
        executablePath = this.lilypondExecutablePath;
		^versionString ?? {
			str = (executablePath + "--version").unixCmdGetStdOut;
			str.copyRange(*[str.findRegexp("\\s[0-9]")[0][0]+1, str.find("\n")-1]);
		};
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE CLASS PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • *outputDirectory

    Fosc.outputDirectory;
    -------------------------------------------------------------------------------------------------------- */
    *outputDirectory {
        ^"%/fosc-output".format(Platform.userConfigDir);
    }
    /* --------------------------------------------------------------------------------------------------------
    • *rootDirectory

    Fosc.rootDirectory;
    -------------------------------------------------------------------------------------------------------- */
    *rootDirectory {
        ^"%/fosc".format(Platform.userExtensionDir);
    }
    /* --------------------------------------------------------------------------------------------------------
    • *stylesheetDirectory

    Fosc.stylesheetDirectory;
    -------------------------------------------------------------------------------------------------------- */
    *stylesheetDirectory {
        ^"%/stylesheets".format(this.rootDirectory);
    }
    /* --------------------------------------------------------------------------------------------------------
    • *lilypondExecutablePath

    Fosc.lilypondExecutablePath;
    -------------------------------------------------------------------------------------------------------- */ 
    *lilypondExecutablePath {
        if (lilypondExecutablePath.isNil || { File.exists(lilypondExecutablePath).not }) {
            error("Lilypond executable not found at: %.".format(lilypondExecutablePath));
            ^nil;
        } {
            ^lilypondExecutablePath;  
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • *lilypondExecutablePath_

    The executable path should be set in the user startup file, e.g.:

    Fosc.lilypondExecutablePath = "/Applications/LilyPond.app/Contents/Resources/bin/lilypond";

    or:

    Fosc.lilypondExecutablePath = "/usr/local/bin/lilypond";
    -------------------------------------------------------------------------------------------------------- */ 
    *lilypondExecutablePath_ { |path|
        lilypondExecutablePath = path;
    }
}
