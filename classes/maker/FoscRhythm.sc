/* ------------------------------------------------------------------------------------------------------------
• FoscRhythm


• Example 1

a = FoscRhythm(1/4, #[-2, 2]);
a.value.do { |each| each.str.postln };

a = FoscRhythm(1/4, #[-2, 3]);
a.value.do { |each| each.str.postln };
a.show;


• Example 2

Can be nested.

a = FoscRhythm(3/16, [1, -2, FoscRhythm(2, #[1, 2, 4])]);
a.show;


• Example 3

Ircam-style rhythm-tree syntax.

a = FoscRhythm(1/4, #[1, -2, [2, [1, 2, 4]]]);
a.show;


• Example 4 !!! DEPRECATED

Floats are interpreted as ties.

a = FoscRhythm(3/16, [1, 2, [2, [2.0, -3]]]);
a.show;
------------------------------------------------------------------------------------------------------------ */
FoscRhythm : FoscTreeContainer {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <writtenDuration, <offset, <offsetsAreCurrent=false;
    var mixin, preProlatedDuration;
    *new { |duration, items|
        if (duration.isKindOf(FoscRhythm)) { ^duration };
        duration = FoscDuration(duration ? 1);
        items = items ? [];
        ^super.new.initFoscRhythm(duration, items);
    }
    initFoscRhythm { |duration, items|
        preProlatedDuration = FoscDuration(duration ? 1);
        mixin = FoscRhythmMixin();
        items = items.collect { |each, i|
            case 
            { each.isInteger } { FoscRhythmLeaf(each) }
            { each.isKindOf(FoscTreeNode) } { each }
            { each.isSequenceableCollection } { FoscRhythm(*each) }
            { throw("%::new: bad value: %.".format(this.species, each.asCompileString)) };
        };
        this.addAll(items);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • doesNotUnderstand

    Delegate to FoscRhythmMixin.
    -------------------------------------------------------------------------------------------------------- */
    doesNotUnderstand { |selector ... args|
        if (mixin.respondsTo(selector)) {
            ^mixin.performList(selector, [this].addAll(args));
        } {
            DoesNotUnderstandError(this, selector, args).throw;
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • value
    -------------------------------------------------------------------------------------------------------- */
    value { |pulseDuration=1|
        var result;
        pulseDuration = FoscDuration(pulseDuration);
        result = this.prRecurse(this, pulseDuration * this.prGetPreprolatedDuration);
        ^result;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • ==

    a = FoscRhythm(FoscDuration(2, 4), [-2, 5]);
    b = FoscRhythm(FoscDuration(2, 4), [-2, 5]);
    c = FoscRhythm(FoscDuration(2, 4), [2, 5]);
    a == b;     // true
    a == c;     // false
    -------------------------------------------------------------------------------------------------------- */
    == { |expr|
        if (expr.isKindOf(this.species).not) { ^false };
        if (this.duration != expr.duration) { ^false };
        if (this.items.size != expr.items.size) { ^false };
        expr.items.do { |each, i|
            if (items[i].prGetPreprolatedDuration != each.prGetPreprolatedDuration) { ^false };
            if (items[i].isPitched != each.isPitched) { ^false };
        };
        ^true;
    }
    /* --------------------------------------------------------------------------------------------------------
    • !=

    a = FoscRhythm(FoscDuration(2, 4), [-2, 5]);
    b = FoscRhythm(FoscDuration(2, 4), [-2, 5]);
    c = FoscRhythm(FoscDuration(2, 4), [2, 5]);
    a != b;     // false
    a != c;     // true
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • illustrate

    Illustrates FoscRhythm.
    
    Returns LilyPond file.
    -------------------------------------------------------------------------------------------------------- */
    illustrate { |stretch=1|
        var selection, selections;
        selection = this.value.copy;
        selections = selection.leaves.groupBy { |a, b| a.parent != b.parent };
        selections.do { |each| each.beam(beamRests: false) };
        ^FoscLilypondFile.rhythm([selection], stretch: stretch);
    }
    /* --------------------------------------------------------------------------------------------------------
    • inspect

    a = FoscRhythm(2/4, #[-2, [2, [-2, 3]], 3]);
    a.inspect;
    -------------------------------------------------------------------------------------------------------- */
    inspect {
        this.do { |each|
            each.depth.do { Post.tab };
            if (each.parent.isNil) {
                Post << each.prGetPreprolatedDuration.str << nl;
            } {
                Post << each.prGetPreprolatedDuration.numerator << nl;
            };
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • show

    a = FoscRhythm(3/8, #[-2, 2, 3]);
    a.show;

    a = FoscRhythm(3/16, [1, -2, FoscRhythm(2, #[1, 2, 4])]);
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    show {
        var lilypondFile;
        lilypondFile = this.illustrate;
        lilypondFile.show;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • duration

    • Example 1

    a = FoscRhythm([3, 4], [1, 2, 2, 1, 1]);
    a.duration.cs;

    • Example 2

    b = FoscRhythm(4, [-3, 2]);
    a = FoscRhythm([3, 4], [1, 2, b]);
    a.duration.cs;
    b.duration.cs;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • durations


    • Example 1

    a = FoscRhythm(4/4, #[-3, 2, 2]);
    a.durations.do { |each| each.cs.postln };


    • Example 2

    a = FoscRhythm(1/4, #[1, 2, [2, [2, -3]]]);
    a.durations.do { |each| each.cs.postln };
    -------------------------------------------------------------------------------------------------------- */
    durations {
        var result;
        result = [];
        this.offsets.doAdjacentPairs { |a, b| result = result.add(b - a) };
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • improperParentage

    • Example 1

    b = FoscRhythm(4, [-3, 2]);
    a = FoscRhythm([3, 4], [1, 2, b]);
    a.improperParentage;
    b.improperParentage;
    b.items.last.improperParentage;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • offsets


    • Example 1

    a = FoscRhythm(4/4, #[-3, 2, 2]);
    a.offsets.do { |each| each.cs.postln };


    • Example 2

    a = FoscRhythm(1/4, #[1, 2, [2, [2, -3]]]);
    a.offsets.do { |each| each.cs.postln };
    -------------------------------------------------------------------------------------------------------- */
    offsets {
        var result;
        result = Set[];
        this.leaves.do { |leaf|
            // DEPRECATED if (leaf.isTiedToPrevLeaf.not) { result.add(leaf.startOffset) };
            result.add(leaf.startOffset);
        };
        result.add(this.stopOffset);
        result = result.as(Array).sort;
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • parentageRatios

    A sequence describing the relative durations of the nodes in a node's improper parentage.

    The first item in the sequence is the preprolatedDuration of the root node, and subsequent items are pairs of the preprolated duration of the next node in the parentage and the total preprolated_duration of that node and its siblings.

    Returns array.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • prolation

    Prolation of rhythm tree node.

    Returns multiplier.
    

    • Example 1

    b = FoscRhythm(4, #[-3, 2]);
    a = FoscRhythm(3/4, [1, 2, b]);

    a.prolation.cs;
    b.prolation.cs;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • prolations

    Prolations of rhythm tree node.

    Returns array.


    • Example 1

    b = FoscRhythm(4, #[-3, 2]);
    a = FoscRhythm(3/4, [1, 2, b]);

    a.prolations.do { |each| each.cs.postln };
    b.prolations.do { |each| each.cs.postln };
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • properParentage


    • Example 1
    
    b = FoscRhythm(4, #[-3, 2]);
    a = FoscRhythm(3/4, [1, 2, b]);

    a.properParentage;
    b.properParentage;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • startOffset

    The starting offset of a node in a rhythm-tree relative to the root.

    Returns a FoscOffset.


    • Example 1

    a = FoscRhythm(1, #[1, [1, [1, 1]], [1, [1, 1]]]);
    a.do { |node| node.depth.do { Post.tab }; node.startOffset.cs.postln };
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • stopOffset

    The stopping offset of a node in a rhythm-tree relative to the root.

    Returns a FoscOffset.


    • Example 1

    a = FoscRhythm(1, #[1, [1, [1, 1]], [1, [1, 1]]]);
    a.do { |node| "node.depth.do { Post.tab }"; node.stopOffset.cs.postln };
    -------------------------------------------------------------------------------------------------------- */
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prGetContentsDuration
    -------------------------------------------------------------------------------------------------------- */
    prGetContentsDuration {
        if (items.isEmpty) { ^FoscDuration(0) };
        ^items.collect { |each| each.prGetPreprolatedDuration }.sum;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetPreprolatedDuration
    -------------------------------------------------------------------------------------------------------- */
    prGetPreprolatedDuration {
        ^preProlatedDuration;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prRecurse
    -------------------------------------------------------------------------------------------------------- */
    prRecurse { |node, tupletDuration|
        var basicProlatedDuration, basicWrittenDuration, tuplet, contentsDuration, multiplier, selection;
        var tieIndices, leaf, prevLeaf;
        basicProlatedDuration = tupletDuration / node.prGetContentsDuration;
        basicWrittenDuration = basicProlatedDuration.equalOrGreaterPowerOfTwo;
        tuplet = FoscTuplet(1, []);
        node.items.do { |child|
            if (child.isKindOf(this.species)) {
                tuplet.addAll(this.prRecurse(child, child.prGetPreprolatedDuration * basicWrittenDuration));
            } {
                tuplet.addAll(child.(basicWrittenDuration));
            };
        };
        contentsDuration = FoscInspection(tuplet).duration;
        multiplier = tupletDuration / contentsDuration;
        tuplet.multiplier_(multiplier);

        if (tuplet.multiplier == 1) { tuplet.isHidden_(true) };
        selection = FoscSelection([tuplet]);

        ^selection;
    }
}
