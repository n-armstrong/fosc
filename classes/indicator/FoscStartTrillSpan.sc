/* ------------------------------------------------------------------------------------------------------------
• FoscStartTrillSpan (abjad 3.0)
------------------------------------------------------------------------------------------------------------ */
FoscStartTrillSpan : Fosc {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <interval, <pitch, <leftBroken=false, <tweaks;
    var <context='Voice', <parameter='TRILL', <persistent=true, <publishStorageFormat=true;
    *new { |interval, pitch, tweaks|
        if (interval.notNil) { interval = FoscInterval(interval) };
        if (pitch.notNil) { pitch = FoscPitch(pitch) };
        ^super.new.init(interval, pitch, tweaks);
    }
    init { |argInterval, argPitch, argTweaks|
        interval = argInterval;
        pitch = argPitch;
        FoscLilyPondTweakManager.setTweaks(this, argTweaks);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • context

    Gets context. Returns 'Voice'.
    

    • Example 1

    a = FoscStartTrillSpan();
    a.context;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • interval

    Gets interval.


    • Example 1

    a = FoscStartTrillSpan();
    a.interval;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • parameter

    Gets parameter. Returns 'TRILL'.


    • Example 1

    a = FoscStartTrillSpan();
    a.parameter;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • persistent

    Is true.


    • Example 1

    a = FoscStartTrillSpan();
    a.persistent;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • pitch


    Gets pitch.


    • Example 1

    a = FoscStartTrillSpan();
    a.pitch;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • spannerStart

    Is true.


    • Example 1

    a = FoscStartTrillSpan();
    a.spannerStart;
    -------------------------------------------------------------------------------------------------------- */
    spannerStart {
        ^true;
    }
    /* --------------------------------------------------------------------------------------------------------
    • tweaks

    Gets tweaks.


    • Example 1

    a = FoscStartTrillSpan();
    a.tweaks;
    -------------------------------------------------------------------------------------------------------- */
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • ==

    !!!TODO

    Is true when all initialization values of Abjad value object equal the initialization values of ``argument``.
    
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
        var bundle, localTweaks, string, localPitch;
        bundle = FoscLilyPondFormatBundle();
        if (tweaks.notNil) {
            localTweaks = tweaks.prListFormatContributions;
            bundle.after.spannerStarts.addAll(localTweaks);
        };
        string = "\\startTrillSpan";
        if (interval.notNil || { pitch.notNil}) {
            bundle.opening.spanners.add("\\pitchedTrill");
            if (pitch.notNil) {
                localPitch = pitch;
            } {
                localPitch = component.writtenPitch + interval;
            };
            string = "% %".format(string, localPitch.str);
        };
        bundle.after.spannerStarts.add(string);
        ^bundle;
    }
}
