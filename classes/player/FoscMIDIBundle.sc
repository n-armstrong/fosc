/* ------------------------------------------------------------------------------------------------------------
• FoscMIDIBundle

!!!TODO: add articulations ??


a = FoscScoreSegment(Hakon, 'A');
m = FoscLeafMaker().((60..67), [1/8]);
m.leafAt(0).attach(FoscMetronomeMark(1/4, 60));
m.leafAt(0).attach(FoscPlaybackCommand('OR48')); 
m.leafAt(2).attach(FoscPlaybackCommand('TRI')); 
m.leafAt(4).attach(FoscPlaybackCommand('M4')); 
m.leafAt(0).attach(FoscDynamic('pppp'));
a['A'].add(m);

a.play;
------------------------------------------------------------------------------------------------------------ */
FoscMIDIBundle : FoscObject {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <event;
    var manager;
    *new { |logicalTie, manager, amp|
        assert(
            logicalTie.isKindOf(FoscLogicalTie),
            "%:new: can't initialize with: %.".format(this.species, logicalTie);
        );
        //!!!TODO: check that manager is a FoscMIDIPlaybackManager
        ^super.new.init(logicalTie, manager, amp);
    }
    init { |logicalTie, argManager, amp|
        var leaf, midinote, dur, msgs, msg, chan, num, val;

        leaf = logicalTie.head;
        manager = argManager;
        dur = (logicalTie.tail.stopOffsetInSeconds - leaf.startOffsetInSeconds).asFloat;
        midinote = this.prGetMIDINote(leaf);
        msgs = this.prGetCommands(leaf);

        event = (
            type: \foscMIDIBundle,
            midiout: manager.midiOut,
            chan: manager.chan,
            dur: dur,
            midinote: midinote,
            amp: amp
        );

        if (msgs.notNil) {            
            msgs.do { |msgArgs|
                # msg, chan, num, val = msgArgs;
                switch(msg,
                    'control', {
                        event['ctlNum'] = num;
                        event['control'] = val;
                    },
                    'program', {
                        event['progNum'] = num;
                    }
                );
            };
        };
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prGetCommands

    a = FoscNote(60, 1/4);
    x = FoscPlaybackCommand(name: 'sustainOn');
    a.attach(x);
    a.prGetIndicators(prototype: FoscPlaybackCommand);
    -------------------------------------------------------------------------------------------------------- */
    prGetCommands { |leaf|
        var playbackCommands, name, msgs, msgArgs;
        
        playbackCommands = leaf.prGetIndicators(prototype: FoscPlaybackCommand);
        msgs = [];

        if (playbackCommands.notEmpty) {
            playbackCommands.do { |playbackCommand|
                name = playbackCommand.name;
                msgArgs = manager.commands[name];
                if (msgArgs.notNil) { msgs = msgs.add(msgArgs) };
            };
        } {
            ^nil;
        };

        ^msgs;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetMIDINote

    !!!TODO: get soundingPitch/es rather than writtenPitch
    -------------------------------------------------------------------------------------------------------- */
    prGetMIDINote { |leaf|
        var midinote;
        case
        { leaf.isKindOf(FoscNote) } {
            midinote = leaf.writtenPitch.pitchNumber;
            midinote = [midinote];
        }
        { leaf.isKindOf(FoscChord) } {
            midinote = leaf.writtenPitches.pitchNumbers;
        }
        {
            midinote = 'rest';
        };

        ^midinote;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • event
    -------------------------------------------------------------------------------------------------------- */
}

