/* ------------------------------------------------------------------------------------------------------------
• FoscPlaybackCommand

!!!TODO

Specifies a name for the playback command.

Specifies if and how the playback command appears on the score (e.g. as Markup text or graphic, as LilypondComment, etc.)

Is used when mapping a FoscStaff or FoscVoice to a MIDI or scsynth action, e.g.:


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
FoscPlaybackCommand {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • !!!TODO

    Must behave like other objects that can be attached to leaves (indicators, FoscMarkup, FoscComment)
    - responds to attach and detach
    - holds a reference to the leaf to which it is attached
    -------------------------------------------------------------------------------------------------------- */
    var <name;
    classvar <attachments;
    *new { |name|
        //!!!TODO: check that argument types are as expected
        name = name.asSymbol;
        ^super.new.init(name);
    }
    init { |argName|
        name = argName;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • *bindNameToIndicator

    NB: A copy of the indicator is attached to the leaf in FoscObject:attach.

    FoscPlaybackCommand.bindNameToIndicator('OR48', FoscMarkup("OR48"));
    FoscPlaybackCommand.lib;

    m = FoscPlaybackCommand('OR48');
    m.prGetIndicator;
    -------------------------------------------------------------------------------------------------------- */
    *bindNameToAttachment { |name, attachment|
        //!!!TODO: check that argument types are as expected
        if (attachments.isNil) { attachments = () };
        attachments[name] = attachment;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prGetIndicator
    -------------------------------------------------------------------------------------------------------- */
    prGetIndicator {
        if (attachments.isNil) { ^nil };
        ^FoscPlaybackCommand.attachments[name];
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • name
    -------------------------------------------------------------------------------------------------------- */
}
