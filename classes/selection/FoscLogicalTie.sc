/* ------------------------------------------------------------------------------------------------------------
• FoscLogicalTie

A selection of components in a logical tie.
------------------------------------------------------------------------------------------------------------ */
FoscLogicalTie : FoscSelection {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • asCompileString

    m = FoscStaff(FoscLeafMaker().(#[60,60,62,nil], [1/4,2/4,5/4,3/4]));
    m.selectLeaves[0..1].tie;
    m.doLogicalTies { |each| each.cs.postln };
    -------------------------------------------------------------------------------------------------------- */
    asCompileString {
        var formatBlocks;
        formatBlocks = items.collect { |each| "\n\t" ++ each.cs };
        formatBlocks = formatBlocks.join(",");
        ^"%([%\n])".format(this.species, formatBlocks);
    }
    /* --------------------------------------------------------------------------------------------------------
    • at

    Gets item at 'index' in container. Traverses top-level items only.
    
    Returns component or selection.  

    a = FoscLogicalTie([FoscNote(60, 1/4), FoscNote(60, 1/4)]);
    a[0].str;

    b = a[[0, 1]];
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • copySeries

    Gets item at indices in container. Traverses top-level items only.
    
    Returns component or selection.  

    a = FoscLogicalTie([FoscNote(60, 1/4), FoscNote(60, 1/4)]);
    b = a[0..];
    b.items;
    -------------------------------------------------------------------------------------------------------- */
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prAddOrRemoveNotesToAchieveWrittenDuration
    -------------------------------------------------------------------------------------------------------- */
    prAddOrRemoveNotesToAchieveWrittenDuration { |newWrittenDuration|
        var maker, durations, leaf, token, difference, extraLeaves, extraTokens;
        var parent, index, leaves, components, tuplet, logicalTie, duration, leaves_, multiplier;

        newWrittenDuration = FoscDuration(newWrittenDuration);
        maker = FoscLeafMaker();
      
        case
        { newWrittenDuration.isAssignable } {
            this[0].writtenDuration_(newWrittenDuration);
            this[1..].do { |leaf| mutate(leaf).extract };
            this[0].detach(FoscTie);
            this[0].detach(FoscRepeatTie);
        }
        { newWrittenDuration.hasPowerOfTwoDenominator } {
            durations = maker.([60], [newWrittenDuration]);
            items.do { |leaf, i|
                leaf.writtenDuration_(durations[i].writtenDuration);
            };
        
            case { this.size == durations.size } {
                // pass
            }
            { durations.size < this.size } {
                this[durations.size..].do { |leaf| mutate(leaf).extract };
            }
            { this.size < durations.size } {
                this[0].detach(FoscTie);
                this[0].detach(FoscRepeatTie);
                difference = durations.size - this.size;
                extraLeaves = this[0] ! difference;
                extraLeaves.do { |extraLeaf|
                    extraLeaf.detach(FoscTie);
                    extraLeaf.detach(FoscRepeatTie);
                };
                extraTokens = durations[this.size..];
                extraLeaves.do { |leaf, i| leaf.writtenDuration_(extraTokens[i].writtenDuration) };
                parent = this.last.prGetParentage.parent;
                index = parent.indexOf(this.last);
                parent.insert(index + 1, extraLeaves);
                leaves = this.leaves ++ FoscSelection(extraLeaves);
                tie(leaves);
            };
        } {
            components = maker.([60], newWrittenDuration);
            assert(components[0].isKindOf(FoscTuplet));
            tuplet = components[0];
            logicalTie = tuplet[0].prGetLogicalTie;
            duration = logicalTie.prGetPreprolatedDuration;
            leaves_ = this.prAddOrRemoveNotesToAchieveWrittenDuration(duration);
            multiplier = tuplet.multiplier;
            tuplet = FoscTuplet(multiplier, []);
            mutate(leaves_).wrap(tuplet);
        };

        ^this[0].prGetLogicalTie;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prFuseLeavesByImmediateParentend(part._fuse())
    
    a = FoscContainer({ FoscNote(60, 1/4) } ! 4);
    a.selectLeaves[1..2].prAttachTieToLeaves;
    b = a.selectLogicalTies[1];
    b.prFuseLeavesByImmediateParent;
    a.show;

    a = FoscStaff([FoscLeafMaker().(#[60,60], 1/4), FoscTuplet(2/3, { FoscNote(60, 1/8) } ! 3)]);
    a.selectLeaves[0..2].prAttachTieToLeaves;
    b = a.selectLogicalTies[0];
    b.prFuseLeavesByImmediateParent;
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    prFuseLeavesByImmediateParent {
        var result, parts;
        result = [];
        parts = this.prGetLeavesGroupedByImmediateParents;
        parts.do { |part| result = result.add(part.prFuseLeaves) };
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetLeavesGroupedByImmediateParents

    a = FoscStaff([FoscLeafMaker().(#[60,62], 1/4), FoscTuplet(2/3, { FoscNote(62, 1/8) } ! 3)]);
    a.selectLeaves[1..2].prAttachTieToLeaves;
    // a.show;
    b = a.selectLogicalTies[1];
    b.prGetLeavesGroupedByImmediateParents;
    
    a = FoscStaff({ FoscNote(60, 1/4) } ! 4);
    a.selectLeaves[1..2].prAttachTieToLeaves;
    b = a.selectLogicalTies[1];
    b.prGetLeavesGroupedByImmediateParents;
    -------------------------------------------------------------------------------------------------------- */
    prGetLeavesGroupedByImmediateParents {
        var parts;
        parts = items.separate { |a, b| a.parent.hash != b.parent.hash };
        parts = parts.collect { |each| FoscSelection(each) };
        ^parts;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prScale

    • Example 1

    a = FoscStaff([b = FoscLogicalTie([FoscNote(60, 1/4), FoscNote(60, 1/8)])]);
    a.prScale(4);
    a.show;

    • Example 2

    b = FoscLogicalTie([FoscNote(60, 1/4), FoscNote(60, 1/4)]);
    a = FoscStaff([b]);
    a.prScale(2.5);
    a.show;

    • Example 3

    b = FoscLogicalTie([FoscNote(60, 1/4), FoscNote(60, 1/4)]);
    a = FoscStaff([b]);
    a.prScale(FoscMultiplier(1, 3));
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    prScale { |multiplier|
        var newDuration;
        newDuration = this.writtenDuration * multiplier;
        ^this.prAddOrRemoveNotesToAchieveWrittenDuration(newDuration);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • head

    Reference to element 0 in logical tie.
    
    Returns component.
    
    a = FoscContainer([FoscNote(60, 1/4), FoscNote(60, 1/4)]);
    b = FoscLogicalTie(a[0..]);
    b.head;
    -------------------------------------------------------------------------------------------------------- */
    head {
        if (items.notNil) { ^items[0] };
    }
    /* --------------------------------------------------------------------------------------------------------
    • isPitched

    Is true when logical tie head is a note or chord.
    
    Returns true or false.

    
    a = FoscLogicalTie([FoscNote(60, 1/4), FoscNote(60, 1/4)]);
    a.isPitched;

    a = FoscLogicalTie([FoscRest(1), FoscRest(1)]);
    a.isPitched;
    -------------------------------------------------------------------------------------------------------- */
    isPitched {
        [FoscNote, FoscChord].do { |type| if (this.head.isKindOf(type)) { ^true } };
        ^false;
    }
    /* --------------------------------------------------------------------------------------------------------
    • isTrivial
    
    Is true when length of logical tie is less than or equal to 1.
    
    Returns true or false.
    
    a = FoscLogicalTie([FoscNote(60, 1/4), FoscNote(60, 1/4)]);
    a.isTrivial;

    a = FoscLogicalTie([FoscNote(60, 1/4)]);
    a.isTrivial;
    -------------------------------------------------------------------------------------------------------- */
    isTrivial {
        ^(this.size <= 1);
    }
    /* --------------------------------------------------------------------------------------------------------
    • leaves

    Leaves in logical tie.

    Returns selection.

    a = FoscLogicalTie([FoscNote(60, 1/4), FoscNote(60, 1/4)]);
    a.leaves;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • tail
    
    Reference to element -1 in logical tie.
    
    Returns component.
    
    a = FoscLogicalTie([FoscNote(60, 1/4), FoscNote(60, 1/4)]);
    a.tail;
    -------------------------------------------------------------------------------------------------------- */
    tail {
        if (items.notNil) { ^items[items.lastIndex] };
    }
    /* --------------------------------------------------------------------------------------------------------
    • writtenDuration

    Sum of written duration of all components in logical tie.

    Returns duration.
    
    a = FoscLogicalTie([FoscNote(60, 1/4), FoscNote(60, 2/4)]);
    a.writtenDuration.cs;
    -------------------------------------------------------------------------------------------------------- */
    writtenDuration {
        ^items.collect { |each| each.writtenDuration }.sum;
    }
    /* --------------------------------------------------------------------------------------------------------
    • writtenPitch

    Written pitch of logical tie if logical is pitched.

    Returns pitch.
    
    a = FoscLogicalTie([FoscNote(60, 1/4), FoscNote(60, 2/4)]);
    a.writtenPitch.cs;
    -------------------------------------------------------------------------------------------------------- */
    writtenPitch {
        if (this.isPitched) { ^this.head.writtenPitch } { ^nil };
    }
    /* --------------------------------------------------------------------------------------------------------
    • writtenPitch_

    Sets written pitch of logical tie if logical is pitched.
    
    a = FoscLogicalTie([FoscNote(60, 1/4), FoscNote(60, 2/4)]);
    a.writtenPitch_(61);
    a.writtenPitch.cs;
    -------------------------------------------------------------------------------------------------------- */
    writtenPitch_ { |pitch|
        if (this.isPitched) { this.doLeaves { |leaf| leaf.writtenPitch_(pitch) } };
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • toTuplet

    Changes logical tie to tuplet.

    a = FoscStaff(FoscLeafMaker().(#[60,62,64], [1/8, 5/16, 1/4]));
    //a.show;
    m = a[1].prGetLogicalTie;
    m.toTuplet(#[-2,1,1,1,2]);
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    toTuplet { |proportions|
        var targetDuration, prolatedDuration, basicWrittenDuration, writtenDurations, maker, writtenPitch;
        var denominator, noteDurations, pitches, notes, tuplet;
        
        if (this.head.parent.isNil) {
            ^throw("%:%: leaves must have a parent.".format(this.species, thisMethod.name));
        };
        
        if (this.areContiguousSameParent.not) {
            ^throw("%:%: leaves must all be children of the same parent."
                .format(this.species, thisMethod.name));
        };
        
        targetDuration = this.prGetPreprolatedDuration;
        prolatedDuration = targetDuration / proportions.sum;
        basicWrittenDuration = prolatedDuration.equalOrGreaterPowerOfTwo;   
        writtenDurations = proportions.abs.collect { |each| basicWrittenDuration * each };
        maker = FoscLeafMaker();
        writtenPitch = if (this.isPitched) { this.head.writtenPitch } { 60 };
        
        if (writtenDurations.every { |each| each.isAssignable }) {
            notes = writtenDurations.collect { |duration, i|
                if (proportions[i] > 0) { FoscNote(writtenPitch, duration) } { FoscRest(duration) };
            };
        } {
            denominator = targetDuration.denominator;
            pitches = proportions.collect { |each, i| if (i > 0) { writtenPitch } { nil } };
            noteDurations = proportions.abs.collect { |each| FoscDuration(each, denominator) };
            notes = maker.([writtenPitch], noteDurations);
        };

        tuplet = FoscTuplet.newFromDuration(targetDuration, notes);
        tuplet.normalizeMultiplier;
        
        this.do { |leaf|
            leaf.detach(FoscTie);
            leaf.detach(FoscRepeatTie);
        };

        mutate(this).replace(tuplet);
        
        ^tuplet;
    }
    // toTuplet { |proportions|
    //     var targetDuration, prolatedDuration, basicWrittenDuration, writtenDurations, maker, writtenPitch;
    //     var denominator, noteDurations, notes, tuplet;

    //     if (this.areContiguousSameParent.not) {
    //         ^throw("%:%: leaves must all be children of the same parent."
    //             .format(this.species, thisMethod.name));
    //     };
        
    //     targetDuration = this.prGetPreprolatedDuration;
    //     prolatedDuration = targetDuration / proportions.sum;
    //     basicWrittenDuration = prolatedDuration.equalOrGreaterPowerOfTwo;   
    //     writtenDurations = proportions.collect { |each| basicWrittenDuration * each };
    //     maker = FoscLeafMaker();
    //     writtenPitch = if (this.isPitched) { this.head.writtenPitch } { 60 };
        
    //     if (writtenDurations.every { |each| each.isAssignable }) {
    //         notes = writtenDurations.collect { |duration| FoscNote(writtenPitch, duration) };
    //     } {
    //         denominator = targetDuration.denominator;
    //         noteDurations = proportions.collect { |each| FoscDuration(each, denominator) };
    //         notes = maker.([writtenPitch], noteDurations);
    //     };

    //     tuplet = FoscTuplet.newFromDuration(targetDuration, notes);
        
    //     this.do { |leaf|
    //         leaf.detach(FoscTie);
    //         leaf.detach(FoscRepeatTie);
    //     };

    //     mutate(this).replace(tuplet);
        
    //     ^tuplet;
    // }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // TO BE DEPRECATED
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prAllLeavesAreInSameParent
    !!!TODO: DEPRECATE

    Is true when all leaves in logical tie are in same parent.

    Returns true or false.

    
    a = FoscContainer([FoscNote(60, 1/4), FoscNote(60, 1/4)]);
    b = FoscLogicalTie(a[0..]);
    b.prAllLeavesAreInSameParent;

    a = FoscLogicalTie([FoscNote(60, 1/4), FoscNote(60, 1/4)]);
    a.prAllLeavesAreInSameParent;   // leaves have no parent
    -------------------------------------------------------------------------------------------------------- */
    // prAllLeavesAreInSameParent {
    //     var leaves, parent;
    //     leaves = this.leaves;
    //     parent = leaves[0].parent;
    //     leaves.do { |leaf| if (leaf.parent.isNil || { leaf.parent != parent }) { ^false } };
    //     ^true;
    // }
    /* --------------------------------------------------------------------------------------------------------
    • tieSpanner

    Tie spanner governing logical tie.
    
    Returns tie spanner.
    
    a = FoscContainer([FoscNote(60, 1/4), FoscNote(60, 1/4)]);
    b = FoscSelection(a[0..]);
    b.attach(FoscTie());
    FoscLogicalTie(b.items).tieSpanner;

    a = FoscContainer([FoscNote(60, 1/4), FoscNote(60, 1/4)]);
    c = FoscLogicalTie(a[0..]);
    c.tieSpanner;  // nil: a FoscTie has not been attached to the selection
    -------------------------------------------------------------------------------------------------------- */
    // tieSpanner {
    //     var type, tieSpanner;
    //     if (this.size > 1) {
    //         type = [FoscTie];
    //         block { |break|
    //             this[0].prGetParentage.items.do { |component|
    //                 try {
    //                     tieSpanner = component.prSpanner(type);
    //                     break.value;
    //                 };
    //             };
    //         };
    //         ^tieSpanner;
    //     };
    // }
}
