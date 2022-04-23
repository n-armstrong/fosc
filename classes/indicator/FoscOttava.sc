/* ------------------------------------------------------------------------------------------------------------
• FoscOttava (abjad 3.0)

Attaches ottava indicators.


• Example 1

a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/8]));
a[0..].ottava;
a.show;
------------------------------------------------------------------------------------------------------------ */
FoscOttava : Fosc {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <n, <formatSlot, <tweaks;
    var <persistent=true, <publishStorageFormat=true;
    *new { |n=0, formatSlot='before'|
        if (n.notNil) {
            assert(n.isInteger, "FoscOttava:new: n must be an integer: %.".format(n));
        };
        if (formatSlot.notNil) {
            assert(
                #['before', 'after'].includes(formatSlot),
                "FoscOttava:new: formatSlot must be 'before' or 'after': %.".format(formatSlot)
            );
        };
        ^super.new.init(n, formatSlot);
    }
    init { |argN, argFormatSlot|
        n = argN;
        formatSlot = argFormatSlot;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • formatSlot

    
    • Example 1

    a = FoscOttava();
    a.formatSlot;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • n

    • Example 1

    a = FoscOttava();
    a.n;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • persistent

    Is true.


    • Example 1

    a = FoscOttava();
    a.persistent;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • tweaks

    Gets tweaks.


    • Example 1

    a = FoscOttava();
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
        //!!! tweaks not in abjad
        if (tweaks.notNil) {
            localTweaks = tweaks.prListFormatContributions;
            bundle.after.spannerStarts.addAll(localTweaks);
        };
        string = "\\ottava %".format(n);
        if (formatSlot == 'before') {
            bundle.before.commands.add(string);
        } {
            bundle.after.commands.add(string);  
        };
        ^bundle;
    }
}
