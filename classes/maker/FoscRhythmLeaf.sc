/* ------------------------------------------------------------------------------------------------------------
• RhythmTreeLeaf

A rhythm-tree leaf.


• Example 1

a = FoscRhythmLeaf(5);
b = a.(1/16);
b.duration.str;
FoscVoice(b).show;


• Example 2

a = FoscRhythmLeaf(-5);
b = a.(1/16);
b.duration.str;
FoscVoice(b).show;
------------------------------------------------------------------------------------------------------------ */
FoscRhythmLeaf : FoscTreeNode {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <preProlatedDuration, <isPitched, <offset, <offsetsAreCurrent=false;
    // DEPRECATE: var <isTiedToPrevLeaf=false;
    var mixin;
    *new { |preProlatedDuration|
        if (preProlatedDuration.isKindOf(FoscRhythmLeaf)) { ^preProlatedDuration };
        ^super.new.initFoscRhythmLeaf(preProlatedDuration);
    }
    initFoscRhythmLeaf { |argPreProlatedDuration|
        preProlatedDuration = FoscDuration(argPreProlatedDuration.abs);
        isPitched = (argPreProlatedDuration > 0);
        mixin = FoscRhythmMixin();
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • doesNotUnderstand

    Delegate unkown methods to FoscRhythmMixin.
    -------------------------------------------------------------------------------------------------------- */
    doesNotUnderstand { |selector ... args|
        if (mixin.respondsTo(selector)) {
            ^mixin.performList(selector, [this].addAll(args));
        } {
            throw(DoesNotUnderstandError(this, selector, args));
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • value

    Generate Abjad score components.

    ## Returns sequence of components.

    Returns selection.
    
    a = FoscRhythmLeaf(5);
    a.prGetPreprolatedDuration.str;
    b = a.([1, 16]);
    FoscVoice(b).show;

    a = FoscNonreducedFraction(#[2, 4]);
    a = a * 3;
    a.str;
    -------------------------------------------------------------------------------------------------------- */
    value { |pulseDuration|
       var totalDuration, maker;
        pulseDuration = FoscDuration(pulseDuration);
        totalDuration = pulseDuration * this.prGetPreprolatedDuration;
        if (this.isPitched) { ^FoscLeafMaker().([60], [totalDuration]) };
        ^FoscLeafMaker().([nil], [totalDuration]);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    prGetPreprolatedDuration {
        ^preProlatedDuration;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • duration

    • Example 1

    a = FoscRhythm(3/4, [1, 2, 2, 1, 1]);
    a.duration.str;

    • Example 2
    
    b = FoscRhythm(4, [-3, 2]);
    a = FoscRhythm(3/4, [1, 2, b]);
    
    a.duration.str;
    b.duration.str;
    b.items.last.duration.str;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • improperParentage

    • Example 1
    
    b = FoscRhythm(4, [-3, 2]);
    a = FoscRhythm(3/4, [1, 2, b]);
    
    a.improperParentage;
    b.improperParentage;
    b.items.last.improperParentage;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • parentageRatios

    A sequence describing the relative durations of the nodes in a node's improper parentage.

    The first item in the sequence is the preprolated_duration of the root node, and subsequent items are pairs of the preprolated duration of the next node in the parentage and the total preprolated_duration of that node and its siblings.

    Returns array.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • prolation

    Prolation of rhythm tree node.

    Returns multiplier.
    
    b = FoscRhythm(4, [-3, 2]);
    a = FoscRhythm([3, 4], [1, 2, b]);
    a.prolation.pair;
    b.prolation.pair;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • prolations

    Prolations of rhythm tree node.

    Returns array.

    • Example 1
    
    b = FoscRhythm(4, [-3, 2]);
    a = FoscRhythm([3, 4], [1, 2, b]);
    a.prolations.do { |each| each.pair.postln };
    b.prolations.do { |each| each.pair.postln };
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • properParentage

    • Example 1
    
    b = FoscRhythm(4, [-3, 2]);
    a = FoscRhythm([3, 4], [1, 2, b]);
    a.properParentage;
    b.properParentage;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • startOffset

    The starting offset of a node in a rhythm-tree relative to the root.

    Returns a FoscOffset.

    • Example 1

    a = FoscRhythm([1, 1], [1, [1, [1, 1]], [1, [1, 1]]]);
    a.do { |node| node.depth.do { Post.tab }; node.startOffset.pair.postln };
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • stopOffset

    The stopping offset of a node in a rhythm-tree relative to the root.

    Returns a FoscOffset.

    • Example 1

    a = FoscRhythm(1, [1, [1, [1, 1]], [1, [1, 1]]]);
    a.do { |node| node.depth.do { Post.tab }; node.stopOffset.pair.postln };
    -------------------------------------------------------------------------------------------------------- */


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // DEPRECATE
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • isTiedToPrevLeaf_
    -------------------------------------------------------------------------------------------------------- */
    // isTiedToPrevLeaf_ { |bool|
    //     isTiedToPrevLeaf = bool;
    // }
}
