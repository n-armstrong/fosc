/* ------------------------------------------------------------------------------------------------------------
• FoscRepeatTie

Lilypond '\repeatTie' command.
------------------------------------------------------------------------------------------------------------ */
FoscRepeatTie : Fosc {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <direction, <leftBroken, <tweaks;
    var <context='Voice', <persistent=true;
    *new { |direction, leftBroken=false, tweaks|
        direction = direction.toTridirectionalLilypondSymbol(direction);
        ^super.new.init(direction, leftBroken, tweaks);
    }
    init { |argDirection, argLeftBroken, tweaks|
        direction = argDirection;
        leftBroken = argLeftBroken;
        FoscLilyPondTweakManager.setTweaks(this, tweaks);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • context
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • direction
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • leftBroken
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • persistent
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • tweaks
    -------------------------------------------------------------------------------------------------------- */
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • ==
    !!!TODO: not yet implemented
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • asCompileString
    !!!TODO: not yet implemented
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • hash
    !!!TODO: not yet implemented
    -------------------------------------------------------------------------------------------------------- */
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • *prShouldForceRepeatTieUp
    -------------------------------------------------------------------------------------------------------- */
    *prShouldForceRepeatTieUp { |leaf|
        // if not isinstance(leaf, (Note, Chord)):
        //     return False
        // if leaf.written_duration < Duration(1):
        //     return False
        // clef = inspect(leaf).effective(Clef, default=Clef('treble'))
        // if isinstance(leaf, Note):
        //     written_pitches = [leaf.written_pitch]
        // else:
        //     written_pitches = leaf.written_pitches
        // for written_pitch in written_pitches:
        //     staff_position = written_pitch.to_staff_position(clef=clef)
        //     if staff_position.number == 0:
        //         return True
        // return False
        var clef, writtenPitches, staffPosition;
        if (leaf.isPitched.not) { ^false };
        if (leaf.writtenDuration < FoscDuration(1)) { ^false };
        clef = leaf.prGetEffective(FoscClef, default: FoscClef('treble'));
        if (leaf.isKindOf(FoscNote)) {
            writtenPitches = [leaf.writtenPitch];
        } {
            writtenPitches = leaf.writtenPitches;
        };
        writtenPitches.do { |writtenPitch|
            staffPosition = writtenPitch.toStaffPosition(clef);
            if (staffPosition.number == 0) { ^true };
        };
        ^false;
    }
    /* --------------------------------------------------------------------------------------------------------
    • *prTagShow
    -------------------------------------------------------------------------------------------------------- */
    // *prTagShow { |strings|
    //     // return LilyPondFormatManager.tag(
    //     //     strings,
    //     //     deactivate=True,
    //     //     tag=abjad_tags.SHOW_TO_JOIN_BROKEN_SPANNERS,
    //     //     )  
    //     ^FoscLilyPondFormatManager.tag(
    //         strings,
    //         deactivate: true,
    //         tag: 'SHOW_TO_JOIN_BROKEN_SPANNERS'
    //     );
    // }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prGetLilypondFormatBundle
    -------------------------------------------------------------------------------------------------------- */
    prGetLilypondFormatBundle { |component|
        // bundle = LilyPondFormatBundle()
        // if self.tweaks:
        //     strings = self.tweaks._list_format_contributions()
        //     if self.left_broken:
        //         strings = self._tag_show(strings)
        //     bundle.after.spanners.extend(strings)
        // strings = []
        // if self._should_force_repeat_tie_up(component):
        //     string = r'- \tweak direction #up'
        //     strings.append(string)
        // strings.append(r'\repeatTie')
        // if self.left_broken:
        //     strings = self._tag_show(strings)
        // bundle.after.spanners.extend(strings)
        // return bundle
        var bundle, strings, string;
        bundle = FoscLilyPondFormatBundle();
        if (tweaks.notNil) {
            strings = tweaks.prListFormatContributions;
            if (leftBroken) { strings = FoscRepeatTie.prTagShow(strings) };
            bundle.after.spanners.addAll(strings);
        };
        strings = [];
        if (FoscRepeatTie.prShouldForceRepeatTieUp(component)) {
            string = "- \\tweak direction #up";
            strings = strings.add(string);
        };
        strings = strings.add("\\repeatTie");
        if (leftBroken) { strings = FoscRepeatTie.prTagShow(strings) };
        bundle.after.spanners.addAll(strings);
        ^bundle;
    }
}
