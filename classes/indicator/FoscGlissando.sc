/* ------------------------------------------------------------------------------------------------------------
• FoscGlissando (abjad 3.0)

Glissando.


• Example 1

a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/8]));
m = FoscGlissando();
a[0..].attach(m);
a.show;


• Example 2 //!!!TODO

Glissando avoids bend-after indicators.

a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/8]));
b = FoscBendAfter();
m = FoscGlissando();
a[1].attach(b);
a[0..].attach(m);
a.show;
------------------------------------------------------------------------------------------------------------ */
FoscGlissando : Fosc {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <allowRepeats, <allowTies, <parenthesizeRepeats, <stems, <style, <rightBroken, <tweaks;
    var <context='Voice', <persistent=true, <publishStorageFormat=true;
    *new { |allowRepeats=false, allowTies=false, parenthesizeRepeats=false, stems=false, style,
        rightBroken=false, tweaks|
        ^super.new.init(allowRepeats, allowTies, parenthesizeRepeats, stems, style, rightBroken, tweaks);
    }
    init { |argAllowRepeats, argAllowTies, argParenthesizeRepeats, argStems, argStyle, argRightBroken,
        argTweaks|
        //!!!TODO
        // if allow_repeats is not None:
        //     allow_repeats = bool(allow_repeats)
        // self._allow_repeats = allow_repeats
        // if allow_ties is not None:
        //     allow_ties = bool(allow_ties)
        // self._allow_ties = allow_ties
        // if parenthesize_repeats is not None:
        //     parenthesize_repeats = bool(parenthesize_repeats)
        // self._parenthesize_repeats = parenthesize_repeats
        // if right_broken is not None:
        //     right_broken = bool(right_broken)
        // self._right_broken = right_broken
        // if stems is not None:
        //     stems = bool(stems)
        // self._stems = stems
        // if style is not None:
        //     assert isinstance(style, str), repr(style)
        // self._style = style
        // if tweaks is not None:
        //     assert isinstance(tweaks, LilyPondTweakManager), repr(tweaks)
        // self._tweaks = LilyPondTweakManager.set_tweaks(self, tweaks)
        // if zero_padding is not None:
        //     zero_padding = bool(zero_padding)
        // self._zero_padding = zero_padding
        allowRepeats = argAllowRepeats.asBoolean;
        allowTies = argAllowTies.asBoolean;
        parenthesizeRepeats = argParenthesizeRepeats.asBoolean;
        stems = argStems;
        style = argStyle;
        if (style.notNil) { assert(style.isString, receiver: this, method: thisMethod) };
        rightBroken = argRightBroken;
        //startCommand = "\\glissando";
        FoscLilyPondTweakManager.setTweaks(this, argTweaks);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • allowRepeats
    
    Is true when glissando should allow repeated pitches. Otherwise false.

    Defaults to false.


    • Example 1

    Does not allow repeated pitches. This is default behaviour.

    a = FoscStaff(FoscLeafMaker().(#[57,57,59,59,60,60,62,62], [1/8]));
    a[2..3].attach(FoscTie());
    a[4..5].attach(FoscTie());
    m = FoscGlissando(allowRepeats: false);
    a[0..].attach(m);
    a.show;


    • Example 2

    Allows repeated pitches but not ties.

    a = FoscStaff(FoscLeafMaker().(#[57,57,59,59,60,60,62,62], [1/8]));
    a[2..3].attach(FoscTie());
    a[4..5].attach(FoscTie());
    m = FoscGlissando(allowRepeats: true);
    a[0..].attach(m);
    a.show;


    • Example 3

    Allows both repeated pitches and ties.

    a = FoscStaff(FoscLeafMaker().(#[57,57,59,59,60,60,62,62], [1/8]));
    a[2..3].attach(FoscTie());
    a[4..5].attach(FoscTie());
    m = FoscGlissando(allowRepeats: true, allowTies: true);
    a[0..].attach(m);
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • allowTies
    
    Is true when glissando should allow ties.


    • Example 1

    Does not allow repeated pitches (including ties). This is default behaviour.

    a = FoscStaff(FoscLeafMaker().(#[57,57,59,59,60,60,62,62], [1/8]));
    a[2..3].attach(FoscTie());
    a[4..5].attach(FoscTie());
    m = FoscGlissando(allowRepeats: false);
    a[0..].attach(m);
    a.show;


    • Example 2

    Allows repeated pitches but not ties.

    a = FoscStaff(FoscLeafMaker().(#[57,57,59,59,60,60,62,62], [1/8]));
    a[2..3].attach(FoscTie());
    a[4..5].attach(FoscTie());
    m = FoscGlissando(allowRepeats: true);
    a[0..].attach(m);
    a.show;


    • Example 3

    Allows both repeated pitches and ties.

    a = FoscStaff(FoscLeafMaker().(#[57,57,59,59,60,60,62,62], [1/8]));
    a[2..3].attach(FoscTie());
    a[4..5].attach(FoscTie());
    m = FoscGlissando(allowRepeats: true, allowTies: true);
    a[0..].attach(m);
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • context

    Gets context. Returns 'Voice'.
    

    • Example 1

    a = FoscGlissando();
    a.context;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • parenthesizeRepeats
    
    Is true when glissando should parenthesize repeated pitches.

    Defaults to false.


    • Example 1

    Does not parenthesize repeated pitches. Default behaviour.

    a = FoscStaff(FoscLeafMaker().(#[57,57,59,59,60,60,62,62], [1/8]));
    a[2..3].attach(FoscTie());
    a[4..5].attach(FoscTie());
    m = FoscGlissando();
    a[0..].attach(m);
    a.show;


    • Example 2

    Spans and parenthesizes repeated pitches.

    a = FoscStaff(FoscLeafMaker().(#[57,57,59,59,60,60,62,62], [1/8]));
    a[2..3].attach(FoscTie());
    a[4..5].attach(FoscTie());
    m = FoscGlissando(allowRepeats: true, parenthesizeRepeats: true);
    a[0..].attach(m);
    a.show;


    • Example 3

    Parenthesizes but does not span repeated pitches.

    a = FoscStaff(FoscLeafMaker().(#[57,57,59,59,60,60,62,62], [1/8]));
    a[2..3].attach(FoscTie());
    a[4..5].attach(FoscTie());
    m = FoscGlissando(parenthesizeRepeats: true);
    a[0..].attach(m);
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • persistent

    Is true.


    • Example 1

    a = FoscGlissando();
    a.persistent;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • rightBroken

    Is true when spanner is right-broken.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • stems

    Is true when glissando formats stems-only timing marks on non-edge leaves.


    • Example 1

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/8]));
    m = FoscGlissando(stems: true);
    a[0..].attach(m);
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • style

    Gets style.


    • Example 1

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/8]));
    m = FoscGlissando(style: "trill");
    a[0..].attach(m);
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • tweaks

    Gets tweaks.
    -------------------------------------------------------------------------------------------------------- */
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • ==

    Is true when all initialization values of Abjad value object equal the initialization values of 'argument'.
    
    def __eq__(self, argument) -> bool:
        return StorageFormatManager.compare_objects(self, argument)
    -------------------------------------------------------------------------------------------------------- */
    == {
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • asCompileString

    Gets interpreter representation.
    
    def __repr__(self) -> str:
            return StorageFormatManager(self).get_repr_format()
    -------------------------------------------------------------------------------------------------------- */
    asCompileString {
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • hash

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
    prGetLilypondFormatBundle { |leaf|
        var bundle, localTweaks, strings;
        bundle = FoscLilyPondFormatBundle();
        if (tweaks.notNil) {
            localTweaks = tweaks.prListFormatContributions;
            bundle.after.spannerStarts.addAll(localTweaks);
        };
        strings = #["\\glissando"];
        bundle.after.spannerStarts.addAll(strings);
        ^bundle;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • *prIsLastInTieChain
    -------------------------------------------------------------------------------------------------------- */
    *prIsLastInTieChain { |leaf|
        var logicalTie;
        logicalTie = FoscInspection(leaf).logicalTie;
        ^(leaf == logicalTie.last);
    }
    /* --------------------------------------------------------------------------------------------------------
    • *prNextLeafChangesCurrentPitch

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    m = FoscGlissando();
    a[0..].attach(m);
    a.format;
    -------------------------------------------------------------------------------------------------------- */
    *prNextLeafChangesCurrentPitch { |leaf|
        var nextLeaf;
        nextLeaf = leaf.prLeafAt(1);
        case 
        {
            leaf.isKindOf(FoscNote)
            && { nextLeaf.isKindOf(FoscNote) }
            && { leaf.writtenPitch == nextLeaf.writtenPitch }
        } {
            ^false;
        } {
            leaf.isKindOf(FoscChord)
            && { nextLeaf.isKindOf(FoscChord) }
            && { leaf.writtenPitches == nextLeaf.writtenPitches }
        } {
            ^false
        };
        ^true;
    }
    /* --------------------------------------------------------------------------------------------------------
    • *prParenthesizeLeaf
    -------------------------------------------------------------------------------------------------------- */
    *prParenthesizeLeaf { |leaf|
        if (leaf.isKindOf(FoscNote)) {
            leaf.noteHead.isParenthesized = true;
        } {
            if (leaf.isKindOf(FoscChord)) {
                leaf.noteHeads.do { |noteHead| noteHead.isParenthesized = true };
            };
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • *prPreviousLeafChangesCurrentPitch
    -------------------------------------------------------------------------------------------------------- */
    *prPreviousLeafChangesCurrentPitch { |leaf|
        var previousLeaf;
        previousLeaf = leaf.prLeafAt(-1);
        if (leaf.isKindOf(FoscNote) && { previousLeaf.isKindOf(FoscNote) }
            && { leaf.writtenPitch == previousLeaf.writtenPitch }) {
            ^false;
        } {
            if (leaf.isKindOf(FoscChord) && { previousLeaf.isKindOf(FoscChord) }
                && { leaf.writtenPitches == previousLeaf.writtenPitches }) {
                ^false;
            }
        };
        ^true;
    }
}
