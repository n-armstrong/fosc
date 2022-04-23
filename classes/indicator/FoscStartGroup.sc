/* ------------------------------------------------------------------------------------------------------------
• FoscStartGroup (abjad 3.0)

!!!TODO: text argument -> Lilypond 2.19.82 and up
------------------------------------------------------------------------------------------------------------ */
FoscStartGroup : Fosc {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <text, <tweaks;
    var <persistent=true, <publishStorageFormat=true;
    *new { |text, tweaks|
        if (text.notNil) {
            assert(
                [String, Symbol, FoscMarkup].any { |type| text.isKindOf(type) },
                "FoscStartGroup:new: text must be a String, Symbol, or FoscMarkup: %.".format(text);
            );
        };
        ^super.new.init(text, tweaks);
    }
    init { |argText, argTweaks|
        text = FoscMarkup(argText);
        FoscLilyPondTweakManager.setTweaks(this, argTweaks);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • persistent

    Is true.


    • Example 1

    a = FoscStartGroup();
    a.persistent;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • spannerStart

    Is true.


    • Example 1

    a = FoscStartGroup();
    a.spannerStart;
    -------------------------------------------------------------------------------------------------------- */
    spannerStart {
        ^true;
    }
    /* --------------------------------------------------------------------------------------------------------
    • tweaks

    Gets tweaks.


    • Example 1

    a = FoscStartGroup();
    a.tweaks;
    -------------------------------------------------------------------------------------------------------- */
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • ==

    !!!TODO

    Is true when all initialization values of Abjad value object equal the initialization values of 'argument'.
    
    def __eq__(self, argument) -> bool:
        return StorageFormatManager.compare_objects(self, argument)
    -------------------------------------------------------------------------------------------------------- */
    == {
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • asCompileString

    !!!TODO

    Gets interpreter representation.

    def __repr__(self) -> str:
        return StorageFormatManager(self).get_repr_format()
    -------------------------------------------------------------------------------------------------------- */
    asCompileString {
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • hash

    !!!TODO

    Hashes Abjad value object.

    def __hash__(self) -> int:
        hash_values = StorageFormatManager(self).get_hash_values()
        try:
            result = hash(hash_values)
        except TypeError:
            raise TypeError(f'unhashable type: {self}')
        return result
    -------------------------------------------------------------------------------------------------------- */
    hash {
        ^this.notYetImplemented(thisMethod);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prGetLilypondFormatBundle
    -------------------------------------------------------------------------------------------------------- */
    prGetLilypondFormatBundle { |component|
        var bundle, localTweaks, string;
        bundle = FoscLilyPondFormatBundle();
        if (tweaks.notNil) {
            localTweaks = tweaks.prListFormatContributions;
            bundle.after.spannerStarts.addAll(localTweaks);
        };
        //!!! lilypond 2.19.82 only
        // if (text.notNil) {
        //     string = "- \\tweak HorizontalBracketText.text %".format(text.format);
        //     bundle.after.spannerStarts.add(string);
        // };
        string = "\\startGroup";
        bundle.after.spannerStarts.add(string);
        ^bundle;
    }
}




