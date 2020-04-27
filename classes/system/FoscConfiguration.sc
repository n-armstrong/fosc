/* ------------------------------------------------------------------------------------------------------------
• FoscConfiguration

FoscConfiguration.getLilypondVersionString
------------------------------------------------------------------------------------------------------------ */
FoscConfiguration {
    classvar lilypondExecutablePath="lilypond";
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • *initClass
    -------------------------------------------------------------------------------------------------------- */
    *initClass {
        var stylesheetsDir, returnCode;
        if (File.exists(this.foscOutputDirectory).not) {
            File.mkdir(this.foscOutputDirectory);
        };
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • *getLilypondVersionString
    
	FoscConfiguration.getLilypondVersionString;
    -------------------------------------------------------------------------------------------------------- */
    *getLilypondVersionString {
    	var executablePath, str, versionString;
		//TODO: get executable path using FoscIOManager
		//executablePath = this.executablePath;
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
    • *lilypondExecutablePath

    FoscConfiguration.lilypondExecutablePath;
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

    The executable path should be set in the user's startup file. e.g.:

    FoscConfiguration.lilypondExecutablePath = "/Applications/LilyPond.app/Contents/Resources/bin/lilypond";

    or:

    FoscConfiguration.lilypondExecutablePath = "/usr/local/bin/lilypond";
    -------------------------------------------------------------------------------------------------------- */ 
    *lilypondExecutablePath_ { |path|
        lilypondExecutablePath = path;
    }
    /* --------------------------------------------------------------------------------------------------------
    • *foscOutputDirectory
    -------------------------------------------------------------------------------------------------------- */
    *foscOutputDirectory {
        ^(Platform.userConfigDir ++ "/fosc-output");
    }
    /* --------------------------------------------------------------------------------------------------------
    • *foscRootDirectory
    -------------------------------------------------------------------------------------------------------- */
    *foscRootDirectory {
        ^(Platform.userExtensionDir ++ "/fosc");
    }
    /* --------------------------------------------------------------------------------------------------------
    • *foscStylesheetDirectory
    -------------------------------------------------------------------------------------------------------- */
    *foscStylesheetDirectory {
        ^this.foscRootDirectory ++ "/stylesheets";
    }
}
