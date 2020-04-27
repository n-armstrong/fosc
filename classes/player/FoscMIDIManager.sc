/* ------------------------------------------------------------------------------------------------------------
• FoscMIDIManager
------------------------------------------------------------------------------------------------------------ */
FoscMIDIManager {
    classvar defaultCommands;
    var <midiOut, <chan, <commands, <lilypondObjects;
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    *initClass {
        defaultCommands = ();
        // this.mapCommandNameToMIDIAction('sostenutoOff', \control, 66, 0);
        // this.mapCommandNameToMIDIAction('sostenutoOn', \control, 66, 127);
        // this.mapCommandNameToMIDIAction('sustainOff', \control, 64, 0);
        // this.mapCommandNameToMIDIAction('sustainOn', \control, 64, 127);
        // this.mapCommandNameToMIDIAction('treCorde', \control, 67, 0);
        // this.mapCommandNameToMIDIAction('unaCorda', \control, 67, 127);
    }
    *new { |midiOut, chan=0|
        if (midiOut.isNil) {
            midiOut = MIDIOut.newByName("IAC Driver", "IAC Bus 1");
            midiOut.latency_(Server.default.latency);
        };
        ^super.new.init(midiOut, chan);
    }
    init { |argMIDIOut, argChan|
        midiOut = argMIDIOut;
        chan = argChan;
        commands = defaultCommands.copy;
        lilypondObjects = ();
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • mapCommandToLilypondObject
    -------------------------------------------------------------------------------------------------------- */
    mapCommandNameToLilypondObject { |name, lilypondObject|
        lilypondObjects[name] = lilypondObject;
    }
    /* --------------------------------------------------------------------------------------------------------
    • mapCommandToMIDIAction
    -------------------------------------------------------------------------------------------------------- */
    mapCommandNameToMIDIAction { |name, msg, lilypondObject|
        var type, num, val = msg;
        # type, num, val = msg;
        //!!!TODO: check that midiOut responds to 'type'  
        if (val.isNil) {
            commands[name] = [type, chan, num];
        } {
            commands[name] = [type, chan, num, val];
        };
    }
}
