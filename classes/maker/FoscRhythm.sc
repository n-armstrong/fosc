/* ------------------------------------------------------------------------------------------------------------
• FoscRhythm


• Example 1

a = FoscRhythm(1/4, #[-2,2]);
a.selection.do { |each| each.str.postln };
a.show;

a = FoscRhythm(1/4, #[-2,3]);
a.selection.do { |each| each.str.postln };
a.show;


• Example 2

Can be nested.

a = FoscRhythm(3/16, [1, -2, FoscRhythm(2, #[1, 2, 4])]);
a.show;


• Example 3

Ircam-style rhythm-tree syntax.

a = FoscRhythm(1/4, #[1,-2,[2,[1,2,4]]]);
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
            { ^throw("%::new: bad value: %.".format(this.species, each.asCompileString)) };
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

    !!!TODO: DEPRECATE IN FAVOUR OF SELECTION

    a = FoscRhythm(1/4, #[1,-2,[2,[1,2,4]]]);
    b = a.value;
    b.show;
    -------------------------------------------------------------------------------------------------------- */
    value {
        ^this.selection;
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

    a = FoscRhythm(2/4, #[-2, [2, [-2, 3]], 3]);
    a.show(staffSize: 12);
    -------------------------------------------------------------------------------------------------------- */
    illustrate { |paperSize, staffSize, includes|
        ^this.selection.illustrate(paperSize, staffSize, includes);
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
        this.leaves.do { |leaf| result.add(leaf.startOffset) };
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
    • selection

    The starting offset of a node in a rhythm-tree relative to the root.

    Returns a FoscOffset.


    • Example 1

    a = FoscRhythm(1, #[1, [1, [1, 1]], [1, [1, 1]]]);
    a.do { |node| node.depth.do { Post.tab }; node.startOffset.cs.postln };
    -------------------------------------------------------------------------------------------------------- */
    selection {
        var selection, selections;
        
        selection = this.prRecurse(this, this.prGetPreprolatedDuration);
        selections = selection.leaves.partitionBy { |a, b| a.parent != b.parent };
        selections.do { |each| each.beam(beamRests: false) };

        ^selection;
    }
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

    a = FoscRhythm(3/16, [4,5]).selection;
    a.show;

    Fosc
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
/* ------------------------------------------------------------------------------------------------------------
• RhythmLeaf
------------------------------------------------------------------------------------------------------------ */
FoscRhythmLeaf : FoscTreeNode {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <preProlatedDuration, <isPitched, <offset, <offsetsAreCurrent=false;
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
            ^throw(DoesNotUnderstandError(this, selector, args));
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
}
/* ------------------------------------------------------------------------------------------------------------
• FoscRhythmMixin

Shared interface for FoscRhythm and FoscRhythmLeaf.
------------------------------------------------------------------------------------------------------------ */
FoscRhythmMixin {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • duration
    -------------------------------------------------------------------------------------------------------- */
    duration { |node|
        ^(node.prolation * node.prGetPreprolatedDuration);
    }
    /* --------------------------------------------------------------------------------------------------------
    • parentageRatios
    -------------------------------------------------------------------------------------------------------- */
    parentageRatios { |node|
        var result;
        
        result = [];
        
        while { node.parent.notNil } {
            result = result.add([node.prGetPreprolatedDuration, node.parent.prGetContentsDuration]);
            node = node.parent;
        };
        
        result = result.add(node.prGetPreprolatedDuration);
        result = result.reverse;
        
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prolation
    -------------------------------------------------------------------------------------------------------- */
    prolation { |node|
        ^node.prolations.reduce('*');
    }
    /* --------------------------------------------------------------------------------------------------------
    • prolations
    -------------------------------------------------------------------------------------------------------- */
    prolations { |node|
        var prolations, improperParentage;
        
        prolations = [FoscMultiplier(1)];
        improperParentage = node.improperParentage;
        
        improperParentage.doAdjacentPairs { |child, parent|
            prolations = prolations.add(
                FoscMultiplier(parent.prGetPreprolatedDuration, parent.prGetContentsDuration);
            );
        };
        
        ^prolations;
    }
    /* --------------------------------------------------------------------------------------------------------
    • startOffset
    -------------------------------------------------------------------------------------------------------- */
    startOffset { |node|
        node.prUpdateOffsetsOfEntireTree;
        ^node.offset;
    }
    /* --------------------------------------------------------------------------------------------------------
    • stopOffset
    -------------------------------------------------------------------------------------------------------- */
    stopOffset { |node|
        ^(node.startOffset + node.duration);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prUpdateOffsetsOfEntireTree
    -------------------------------------------------------------------------------------------------------- */
    prUpdateOffsetsOfEntireTree { |node|
        var recurse, offset, root, children, hasChildren;
        
        if (node.offsetsAreCurrent) { ^nil };
        
        recurse = { |container, currentOffset|
            container.instVarPut('offset', currentOffset);
            container.instVarPut('offsetsAreCurrent', true);
            container.items.do { |child|
                if (child.respondsTo('items') && { child.items.notEmpty }) {
                    currentOffset = recurse.(child, currentOffset);
                } {
                    child.instVarPut('offset', currentOffset);
                    child.instVarPut('offsetsAreCurrent', true);
                    currentOffset = currentOffset + child.duration;
                };
            };
            currentOffset;
        };

        root = node.root;
        offset = FoscOffset(0);
        
        try {
            children = node.items;
            hasChildren = children.notEmpty;
        } {
            hasChildren = false;
        };

        if (node === root && { hasChildren.not }) {
            node.instVarPut('offset', offset);
            node.instVarPut('offsetsAreCurrent', true);
        } {
            recurse.(root, offset);
        };
    }
}

