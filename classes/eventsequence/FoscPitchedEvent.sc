
/* --------------------------------------------------------------------------------------------------------
• FoscPitchedEvent

A FoscEvent which indicates the onset of a period of pitched material in a FoscEventSequence .

a = FoscPitchedEvent(1, #[60, 61, 64]);
a.inspect;

a = FoscPitchedEvent(1, #[60, 61, 64], [FoscDynamic('p')]);
a.inspect;
-------------------------------------------------------------------------------------------------------- */
FoscPitchedEvent : FoscEvent {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <pitches, <amp, <bundleActions;
    *new { |offset, pitches, attachments, index, amp, bundleActions|
        ^super.new(offset, attachments, index).initFoscPitchedEvent(pitches, amp, bundleActions);
    }
    initFoscPitchedEvent { |argPitches, argAmp, argBundleActions|
        // pitches = argPitches ?? { [] };
        // if (pitches.isKindOf(FoscPitchSegment)) { pitches = pitches.items };
        // if (pitches.isKindOf(FoscPitch)) { pitches = [pitches] };
        // pitches = pitches.collect { |each| FoscPitch(each) };
        pitches = FoscPitchParser(argPitches);
        amp = argAmp ?? 0.5;
        bundleActions = argBundleActions ? [];
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • ==
    Is true when `expr` is a pitched q-event with offset, pitches, attachments and index equal to those of this pitched q-event. Otherwise false.
    
    Returns true or false.

    FoscPitchedEvent(1, #[60, 61]) == FoscPitchedEvent(1, #[60, 61]);

    FoscPitchedEvent(1, #[60, 61]) == FoscPitchedEvent(1, #[60]);
    -------------------------------------------------------------------------------------------------------- */
    == { |expr|
        var bool;
        bool = (
            (this.species == expr.species)
            && { offset == expr.offset }
            && { pitches == expr.pitches }
            && { attachments == expr.attachments }
            && { index == expr.index }
        );
        ^bool;
    }
    /* --------------------------------------------------------------------------------------------------------
    •

    Hashes pitched q-event.
    
    Required to be explicitly redefined on Python 3 if __eq__ changes.

    Returns integer.

    def __hash__(self):
        return super(FoscPitchedEvent, self).__hash__()
    -------------------------------------------------------------------------------------------------------- */
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prFuse

    a = FoscPitchedEvent(1, #[60,64]);
    b = FoscPitchedEvent(1, #[67]);
    a.prFuse(b).inspect;
    -------------------------------------------------------------------------------------------------------- */
    prFuse { |event|
        if (this.offset != event.offset) {
            error("%:%: can't fuse FoscEvents with non-identical offsets: % %."
                .format(this.species, thisMethod.name, this.offset.str, event.offset.str));
        };
        ^this.species.new(
            offset: this.offset,
            pitches: (pitches ++ event.pitches).removeDuplicates,
            attachments: attachments,
            index: index,
            amp: amp,
            bundleActions: (bundleActions ++ event.bundleActions).removeDuplicates
        );
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • attachments

    Attachments of pitched q-event.

    a = FoscPitchedEvent(1, #[60, 61], [FoscDynamic('p')]);
    a.attachments;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • pitches

    Pitches of pitched q-event.

    a = FoscPitchedEvent(1, #[60, 61]);
    a.pitches;
    -------------------------------------------------------------------------------------------------------- */
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS: DISPLAY
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • inspect
    -------------------------------------------------------------------------------------------------------- */
    inspect {
        super.inspect;
        Post.tab << "pitches: " << pitches.collect { |each| each.pitchName } << nl;
        if (attachments.notEmpty) { Post.tab << "attachments: " << attachments << nl };
    }
}
