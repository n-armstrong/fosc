/* ------------------------------------------------------------------------------------------------------------
• FoscGraceContainer

FoscAfterGraceContainer

Grace container.

LilyPond formats grace notes with neither a slash nor a slur.

Fill grace containers with notes, rests or chords.

Attach grace containers to notes, rests or chords.


• Example 1

Grace notes.

a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
c = FoscGraceContainer([FoscNote(60, 1/16), FoscNote(62, 1/16)]);
c[0].attach(FoscArticulation('>'));
a[2].attach(c);
a.show;


• Example 2

Slashed grace note.

a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
c = FoscGraceContainer([FoscNote(60, 1/8)], slashed: true);
c[0].attach(FoscArticulation('>'));
a[2].attach(c);
a.show;


• Example 3

Slurred grace note.

a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
c = FoscGraceContainer([FoscNote(60, 1/8)], slurred: true);
c[0].attach(FoscArticulation('>'));
a[2].attach(c);
a.show;


• Example 4

Slashed and slurred grace note.

a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
c = FoscGraceContainer([FoscNote(60, 1/8)], slashed: true, slurred: true);
c[0].attach(FoscArticulation('>'));
a[2].attach(c);
a.show;
------------------------------------------------------------------------------------------------------------ */
FoscGraceContainer : FoscContainer {
	var <mainLeaf, <slashed, <slurred;
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    *new { |components, slashed=false, slurred=false|
        ^super.new(components).initFoscGraceContainer(slashed, slurred);
    }
    initFoscGraceContainer { |argSlashed, argSlurred|
        slashed = argSlashed;
        slurred = argSlurred;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prAttach
    -------------------------------------------------------------------------------------------------------- */
    prAttach { |leaf|
        if (leaf.isKindOf(FoscLeaf).not) {
            ^throw("%:%: must attach to leaf: %.".format(this.species, thisMethod.name, leaf));
        };
        leaf.instVarPut('graceContainer', this);
        mainLeaf = leaf;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prDetach
    -------------------------------------------------------------------------------------------------------- */
    prDetach { |leaf|
        var localCarrier;
        if (mainLeaf.notNil) {
            localCarrier = mainLeaf;
            localCarrier.instVarPut('graceContainer', nil);
            mainLeaf = nil;
        };
        ^this;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prFormatOpenBracketsSlot
    -------------------------------------------------------------------------------------------------------- */
    prFormatOpenBracketsSlot { |bundle|
        var result, string;
        result = [];
        string = case
        { slashed && slurred.not } { "\\slashedGrace" }
        { slashed.not && slurred } { "\\appoggiatura" }
        { slashed && slurred } { "\\acciaccatura" }
        { slashed.not && slurred.not } { "\\grace" }
        { "\\grace" };
        result = result.add([['graceBrackets', 'open'], ["% {".format(string)]]);
        ^result;
    }
     /* --------------------------------------------------------------------------------------------------------
    • prGetLilypondFormat
    -------------------------------------------------------------------------------------------------------- */
    prGetLilypondFormat {
        this.prUpdateNow(indicators: true);
        ^this.prFormatComponent;
    }
}
