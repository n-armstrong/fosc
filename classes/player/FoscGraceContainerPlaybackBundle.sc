/* ------------------------------------------------------------------------------------------------------------
• FoscGraceContainerPlaybackBundle

!!!TODO: add articulations ??
------------------------------------------------------------------------------------------------------------ */
FoscGraceContainerPlaybackBundle {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    classvar <>graceDuration=0.06;
    var <durs, <midinotes, <articulations, <commands;
    *new { |graceContainer|
        assert(
            graceContainer.isKindOf(FoscGraceContainer),
            "FoscGraceContainerPlaybackBundle:new: can't initialize with: %.".format(graceContainer);
        );
        ^super.new.init(graceContainer);
    }
    init { |graceContainer|
        var notes, comments;

        durs = Array.fill(graceContainer.size, FoscGraceContainerPlaybackBundle.graceDuration);
        midinotes = [];
        articulations = [];
        commands = [];

        //!!!TODO: get soundingPitch/es rather than writtenPitch
        FoscIteration(graceContainer).leaves(graceNotes: true).do { |leaf|
            case
            { leaf.isKindOf(FoscNote) } {
                notes = [leaf.writtenPitch.pitchNumber];
            }
            { leaf.isKindOf(FoscChord) } {
                notes = leaf.writtenPitches.collect { |each| each.pitchNumber };
            }
            {
                notes = 'rest';
            };
            midinotes = midinotes.add(notes);

            //!!!TODO: use FoscLilypondPlaybackCommand
            comments = leaf.prGetIndicators(FoscLilypondComment);
            if (comments.notEmpty) { comments = comments.collect { |comment| comment.str[2..].asSymbol } };
            if (comments.isEmpty) { commands = commands.add(nil) } { commands = commands.add(comments) };
        };
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • duration
    -------------------------------------------------------------------------------------------------------- */
    duration {
        ^durs.sum;
    }
    /* --------------------------------------------------------------------------------------------------------
    • durs
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • midinotes
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • articulations
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • commands
    -------------------------------------------------------------------------------------------------------- */
}
