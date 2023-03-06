/* ------------------------------------------------------------------------------------------------------------
• FoscAfterGraceContainer

After grace container.

LilyPond positions after grace notes at a point 3/4 of the way after the note they follow. The resulting spacing is usually too loose.

Customize afterGraceFraction as shown above.

After grace notes are played at the very end of the note they follow.

Use after grace notes when you need to end a piece of music with grace notes.

After grace notes do not subclass grace notes; but acciacatura containers and appoggiatura containers do subclass grace notes.

LilyPond formats grace notes with neither a slash nor a slur.

Fill grace containers with notes, rests or chords.
s
Attach after grace containers to notes, rests or chords.


• Example 1

After grace notes.

a = FoscLeafMaker().("C4 D4 E4 F4", [1/4]);
l = FoscLilyPondLiteral("#(define afterGraceFraction (cons 15 16))");
a[0].attach(l);
c = FoscAfterGraceContainer([FoscNote("C4", 1/16), FoscNote("D4", 1/16)]);
a[1].attach(c);
a.show;
------------------------------------------------------------------------------------------------------------ */
FoscAfterGraceContainer : FoscContainer {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <carrier;
    /* --------------------------------------------------------------------------------------------------------
    def __init__(self, music=None):
        self._carrier = None
        Container.__init__(self, music)
    -------------------------------------------------------------------------------------------------------- */
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prAttach
    
    def _attach(self, leaf):
        import abjad
        if not isinstance(leaf, abjad.Leaf):
            message = 'must attach to leaf: {!r}.'
            message = message.format(leaf)
            raise TypeError(message)
        leaf._after_grace_container = self
        self._carrier = leaf
    -------------------------------------------------------------------------------------------------------- */
    prAttach { |leaf|
        if (leaf.isKindOf(FoscLeaf).not) {
            ^throw("%:%: must attach to leaf: %.".format(this.species, thisMethod.name, leaf));
        };
        leaf.afterGraceContainer_(this);
        carrier = leaf;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prDetach
    
    def _detach(self):
        if self._carrier is not None:
            carrier = self._carrier
            carrier._after_grace_container = None
            self._carrier = None
        return self
    -------------------------------------------------------------------------------------------------------- */
    prDetach { |leaf|
        var localCarrier;
        if (carrier.notNil) {
            localCarrier = carrier;
            localCarrier.afterGraceContainer_(nil);
            carrier = nil;
        };
        ^this;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prFormatOpenBracketsSlot
    
    def _format_open_brackets_slot(self, bundle):
        result = []
        result.append([('grace_brackets', 'open'), ['{']])
        return tuple(result)
    -------------------------------------------------------------------------------------------------------- */
    prFormatOpenBracketsSlot { |bundle|
        var result;
        result = [];
        result = result.add([['graceBrackets', 'open'], ["{"]]);
        ^result;
    }
}
