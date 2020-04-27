/* ------------------------------------------------------------------------------------------------------------
• FoscMIDIPlaybackManager
------------------------------------------------------------------------------------------------------------ */
FoscSynthPlaybackManager {
    classvar defaultCommands;
    var <defName, <server, <commands;
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    *initClass {
        defaultCommands = (
            // 'sustainOn': { |event|
            //     event.midiout.control(event.chan, 64, 127);
            // }
        );
    }
    *new { |defName='default', server|
        server = server ?? { Server.default };
        ^super.new.init(defName, server);
    }
    init { |argDefName, argServer|
        defName = argDefName;
        server = argServer;
        commands = defaultCommands.copy;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • addCommand
    -------------------------------------------------------------------------------------------------------- */
    addCommand { |name, func|
        if (commands[name].notNil) {
            throw("%:%: command already exists: %.".format(this.species, thisMethod.name, name));
        };
        commands[name] = func;
    }
}
