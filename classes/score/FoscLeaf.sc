/* ---------------------------------------------------------------------------------------------------------
• FoscLeaf

Leaf asbtract baseclass.

Leaves include notes, rests, chords and skips.

x = FoscNote(60, 1/4);
x.format;
x.isPitched;
x.pitch.pitchName;
x.prGetPreprolatedDuration.str;
x.prGetDuration.str; //!!!TODO: BROKEN


a = FoscNote(60, FoscDuration(9, 64));
a.show;
--------------------------------------------------------------------------------------------------------- */
FoscLeaf : FoscComponent {
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	// INIT
	////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <afterGraceContainer, <graceContainer, <leafIndex, <multiplier, <writtenDuration;
	*new { |writtenDuration, multiplier|
        if (writtenDuration.notNil) { writtenDuration = FoscDuration(writtenDuration) };
        if (writtenDuration.isAssignable.not) {
            ^throw("%:new: duration is not assignable: %.".format(this, writtenDuration.str));
        };
        if (multiplier.notNil) { multiplier = FoscMultiplier(multiplier) };
		^super.new.initFoscRhythmLeaf(writtenDuration, multiplier);
	}
	initFoscRhythmLeaf { |argWrittenDuration, argMultiplier|
		writtenDuration = argWrittenDuration;
        multiplier = argMultiplier;
	}
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// PUBLIC INSTANCE METHODS: SPECIAL METHODS
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/* --------------------------------------------------------------------------------------------------------
    • asCompileString


    • Example 1

    FoscRest(1/4).cs;
    -------------------------------------------------------------------------------------------------------- */
    asCompileString {
        ^"%(%)".format(this.species, writtenDuration.str);
    }
    /* --------------------------------------------------------------------------------------------------------
    • copy

    Shallow copies leaf.

    Returns new leaf.
    -------------------------------------------------------------------------------------------------------- */
    copy { |...args|
        var new, newGraceContainer, newAfterGraceContainer;
        new = super.copy(*args);
        new.instVarPut('multiplier', multiplier);
        if (graceContainer.notNil) {
            newGraceContainer = graceContainer.prCopyWithChildren;
            new.attach(newGraceContainer);
        };
        if (afterGraceContainer.notNil) {
            newAfterGraceContainer = afterGraceContainer.prCopyWithChildren;
            new.attach(newAfterGraceContainer);
        };
        ^new;
    }
    /* --------------------------------------------------------------------------------------------------------
    • storeArgs

    Gets new arguments.

    Returns array.


    • Example 1

    FoscLeaf(1/4).storeArgs;
    -------------------------------------------------------------------------------------------------------- */
    storeArgs {
        ^[this.writtenDuration];
    }
    /* --------------------------------------------------------------------------------------------------------
    • str

    String representation of leaf.
    
    Returns string.
   

    • Example 1

    FoscRest(1/4).str;
    -------------------------------------------------------------------------------------------------------- */
    str {
        ^this.prGetCompactRepresentation;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////
	// PUBLIC INSTANCE PROPERTIES
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	/* --------------------------------------------------------------------------------------------------------
    • multiplier

    Gets duration multiplier.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • multiplier_

    Sets duration multiplier.
    -------------------------------------------------------------------------------------------------------- */
    multiplier_ { |multiplier|
        if (multiplier.isKindOf(FoscNonreducedFraction).not) {
            multiplier = FoscMultiplier(multiplier);
        };
        this.instVarPut('multiplier', multiplier);
    }
    /* --------------------------------------------------------------------------------------------------------
	• duration
	-------------------------------------------------------------------------------------------------------- */
	// DEPRECATE
    // duration {
	// 	//^(this.writtenDuration * this.prolation);
 //        ^FoscInspection(this).duration;
	// }
	/* --------------------------------------------------------------------------------------------------------
	• isPitched
	-------------------------------------------------------------------------------------------------------- */
	isPitched {
		^false;
	}
	/* --------------------------------------------------------------------------------------------------------
	• prolation
	-------------------------------------------------------------------------------------------------------- */
	// DEPRECATE
    // prolation {
	// 	var prolation, prolations, node;
	// 	prolations = [FoscMultiplier(1)];
	// 	node = this;
	// 	while { node.parent.notNil } {
	// 		prolations = prolations.add(node.parent.prolation);
	// 		node = node.parent;
	// 	};
	// 	prolation = prolations.reduce('*');
	// 	^prolation;
	// }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prCopyOverrideAndSetFromLeaf
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    prCopyOverrideAndSetFromLeaf { |leaf|
        var newWrappers, newWrapper;
        newWrappers = [];
        if (leaf.respondsTo('overrides') && { leaf.overrides.notNil }) {
            this.instVarPut('overrides', override(leaf).copy);
        };
        if (leaf.respondsTo('lilypondSettingNameManager') && { leaf.lilypondSettingNameManager.notNil }) {
            this.instVarPut('lilypondSettingNameManager', set(leaf).copy);
        };
        leaf.wrappers.do { |wrapper|
            newWrapper = wrapper.copy;
            newWrappers = newWrappers.add(newWrapper);
        }; 
        newWrappers.do { |newWrapper| this.attach(newWrapper) };
    }
    /* --------------------------------------------------------------------------------------------------------
     • prDetachAfterGraceContainer
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    prDetachAfterGraceContainer {
        if (afterGraceContainer.notNil) {
            ^this.detach(this.afterGraceContainer);
        };
        ^nil; //!!!TODO: remove when Fosc::detach is complete
    }
    /* --------------------------------------------------------------------------------------------------------
    • prDetachGraceContainer
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    prDetachGraceContainer {
        if (this.graceContainer.notNil) {
            ^this.detach(this.graceContainer);
        };
        ^nil; //!!!TODO: remove when Fosc::detach is complete
    }
    /* --------------------------------------------------------------------------------------------------------
    • prFormatAfterGraceBody
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    prFormatAfterGraceBody {
        var result;
        result = [];
        if (afterGraceContainer.notNil) {
            if (afterGraceContainer.size >= 1) { result = result.add(afterGraceContainer.format) };
        };
        ^['afterGraceBody', result];
    }
    /* --------------------------------------------------------------------------------------------------------
    • prFormatAfterGraceOpening
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    prFormatAfterGraceOpening {
        var result;
        result = [];
        if (afterGraceContainer.notNil) {
            if (afterGraceContainer.size >= 1) { result = result.add("\\afterGrace") };
        };
        ^['afterGraceOpening', result];
    }
    /* --------------------------------------------------------------------------------------------------------
    • prFormatAfterSlot
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    prFormatAfterSlot { |bundle|
        var result;
        result = [];
        result = result.add(['stemTremolos', bundle.after.stemTremolos]);
        result = result.add(['articulations', bundle.after.articulations]);
        result = result.add(['markup', bundle.after.markup]);
        result = result.add(['spanners', bundle.after.spanners]);
        result = result.add(['spannerStops', bundle.after.spannerStops]);
        result = result.add(['spannerStarts', bundle.after.spannerStarts]);
        result = result.add(['trillSpannerStarts', bundle.after.trillSpannerStarts]);
        result = result.add(['commands', bundle.after.commands]);
        result = result.add(['commands', bundle.after.leaks]);
        result = result.add(this.prFormatAfterGraceBody);
        result = result.add(['comments', bundle.after.comments]);
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prFormatBeforeSlot
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    prFormatBeforeSlot { |bundle|
        var result;
        result = [];
        result = result.add(this.prFormatGraceBody);
        result = result.add(['comments', bundle.before.comments]);
        result = result.add(this.prFormatAfterGraceOpening);
        result = result.add(['commands', bundle.before.commands]);
        result = result.add(['indicators', bundle.before.indicators]);
        result = result.add(['grobReverts', bundle.grobReverts]);
        result = result.add(['grobOverrides', bundle.grobOverrides]);
        result = result.add(['contextSettings', bundle.contextSettings]);
        result = result.add(['spanners', bundle.before.spanners]);
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prFormatCloseBracketsSlot
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    prFormatCloseBracketsSlot { |bundle|
        ^[]
    }
    /* --------------------------------------------------------------------------------------------------------
    • prFormatClosingSlot
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    prFormatClosingSlot { |bundle|
        var result;
        result = [];
        result = result.add(['spanners', bundle.closing.spanners]);
        result = result.add(['commands', bundle.closing.commands]);
        result = result.add(['indicators', bundle.closing.indicators]);
        result = result.add(['comments', bundle.closing.comments]);
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prFormatContentsSlot
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    prFormatContentsSlot { |bundle|
        ^[this.prFormatLeafBody(bundle)];
    }
    /* --------------------------------------------------------------------------------------------------------
    • prFormatGraceBody
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    prFormatGraceBody {
        var result;
        result = [];
        if (graceContainer.notNil) {
            if (graceContainer.size >= 1) { result = result.add(graceContainer.format) };
        };
        ^['graceBody', result];
    }
    /* --------------------------------------------------------------------------------------------------------
    • prFormatLeafBody
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    prFormatLeafBody { |bundle|
        var result;
        //!!!TODO: REMOVE: indent = FoscLilyPondFormatManager.indent;
        result = this.prFormatLeafNucleus[1];
        ^['selfBody', result];
    }
    /* --------------------------------------------------------------------------------------------------------
    • prFormatLeafNucleus
    -------------------------------------------------------------------------------------------------------- */
    prFormatLeafNucleus {
        ^['nucleus', this.prGetBody];
    }
    /* --------------------------------------------------------------------------------------------------------
    • prFormatOpenBracketsSlot
    -------------------------------------------------------------------------------------------------------- */
    prFormatOpenBracketsSlot { |bundle|
        ^[]
    }
    /* --------------------------------------------------------------------------------------------------------
    • prFormatOpeningSlot
    -------------------------------------------------------------------------------------------------------- */
    prFormatOpeningSlot { |bundle|
        var result;

        result = [];
        result = result.add(['comments', bundle.opening.comments]);
        result = result.add(['indicators', bundle.opening.indicators]);
        result = result.add(['commands', bundle.opening.commands]);
        result = result.add(['spanners', bundle.opening.spanners]);
        
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetCompactRepresentation
    -------------------------------------------------------------------------------------------------------- */
    prGetCompactRepresentation {
        ^"(%)".format(this.prGetFormattedDuration);
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetFormatPieces
    -------------------------------------------------------------------------------------------------------- */
    prGetFormatPieces {
        ^this.prGetLilypondFormat.split("\n");
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetFormattedDuration

    a = FoscLeaf(3/8);
    a.prGetFormattedDuration;

    a = FoscLeaf(3/8, multiplier: 3/2);
    a.prGetFormattedDuration;
    -------------------------------------------------------------------------------------------------------- */
    prGetFormattedDuration {
        var durationString;
        durationString = writtenDuration.lilypondDurationString;
        if (multiplier.notNil) {
            ^("% * %").format(durationString, multiplier.str)
        } {
            ^durationString;
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetDurationInSeconds
    
    a = FoscScore([FoscStaff(FoscLeafMaker().(60 ! 4, [1/4]))]);
    a.doLeaves { |leaf| leaf.prGetDurationInSeconds.asFloat.postln };

    a = FoscScore([FoscStaff(FoscLeafMaker().(60 ! 4, [1/4]))]);
    a.leafAt(0).attach(FoscMetronomeMark(1/4, 72));
    a.doLeaves { |each| each.prGetDurationInSeconds.asFloat.postln };
    -------------------------------------------------------------------------------------------------------- */
    prGetDurationInSeconds {
        var mark, result;
        
        mark = this.prGetEffective(FoscMetronomeMark) ?? { FoscMetronomeMark(#[1, 4], 60) };
        
        if (mark.isImprecise.not) {
            result = this.prGetDuration / mark.referenceDuration / mark.unitsPerMinute * 60;
        } {
            ^throw("%:%: can't get duration in seconds - tempo is imprecise."
                .format(this.species, thisMethod.name));
        };
        
        ^FoscDuration(result);
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetLogicalTie

    a = FoscStaff(FoscLeafMaker().(60 ! 4, [1/4]));
    a[0..].tie;
    a[0].prGetLogicalTie.items;


    a = FoscStaff(FoscLeafMaker().(60 ! 4, [5/16]));
    a.doLogicalTies { |each| each.items.postln };

    a = FoscLeafMaker().(60 ! 4, [5/16]);
    a.leaves.do { |leaf| leaf.prGetLogicalTie.items.postln };
    a.doLogicalTies { |each| each.items.postln };
    a.logicalTies.items;
    -------------------------------------------------------------------------------------------------------- */
    prGetLogicalTie {
        var leavesBefore, leavesAfter, currentLeaf, previousLeaf, nextLeaf, leaves;
        
        leavesBefore = [];
        leavesAfter = [];
        currentLeaf = this;

        block { |break|
            loop {
                previousLeaf = currentLeaf.prLeafAt(-1);
                if (previousLeaf.isNil) { break.value };
                
                if (
                    currentLeaf.prHasIndicator(FoscRepeatTie) || { previousLeaf.prHasIndicator(FoscTie) }
                ) {
                    leavesBefore = leavesBefore.insert(0, previousLeaf);
                } {
                    break.value;
                };
                
                currentLeaf = previousLeaf;
            };
        };

        currentLeaf = this;

        block { |break|
            loop {
                nextLeaf = currentLeaf.prLeafAt(1);
                if (nextLeaf.isNil) { break.value };
                
                if (
                    currentLeaf.prHasIndicator(FoscTie) || { nextLeaf.prHasIndicator(FoscRepeatTie) }
                ) {
                    leavesAfter = leavesAfter.add(nextLeaf);
                } {
                    break.value;
                };
                
                currentLeaf = nextLeaf;
            };
        };

        leaves = leavesBefore ++ [this] ++ leavesAfter;

        ^FoscLogicalTie(leaves);
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetMultipliedDuration
    
    a = FoscLeaf(writtenDuration: 1/4, multiplier: 3/2);
    a.prGetMultipliedDuration.str;
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    prGetMultipliedDuration {
        if (multiplier.notNil) { ^FoscDuration(multiplier * writtenDuration) };
        ^writtenDuration;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetPreprolatedDuration

    a = FoscLeaf(writtenDuration: 1/4);
    a.prGetPreprolatedDuration.str;

    a = FoscLeaf(writtenDuration: 1/4, multiplier: 3/2);
    a.prGetPreprolatedDuration.str;
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    prGetPreprolatedDuration {
        ^this.prGetMultipliedDuration;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prIterateTopDown
    -------------------------------------------------------------------------------------------------------- */
    //!!!TODO: deprecate, use new implementation of FoscIterationAgent
    prIterateTopDown {
        ^this;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prLeaf

    a = FoscStaff(FoscLeafMaker().((60..72), [1/4]));
    a[0].cs;
    a[0].prLeafAt(1).cs;
    a[0].prLeafAt(-1).cs;
    a[0].prLeafAt(5).cs;
    a[5].prLeafAt(-3).cs;


    a = FoscStaff(FoscLeafMaker().(#[60,60,62,64,65,65], [1/4,1/24,1/12,1/8,1/4,1/4]));
    m = a.selectLeaves;
    tie(m[0..1]);
    tie(m[4..5]);
    //a.show;

    x = a.leafAt(1);
    x.cs; // correct
    x.prSibling(-1).cs; // correct
    x.prLeafAt(-1).cs; // correct
    x.prGetLogicalTie.components.collect { |each| each.cs };

    x = a.leafAt(0);
    x.prLeafAt(-1).cs; // should be nil
    x.prSibling(-1).cs; // should be nil
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    prLeafAt { |n|
        var sibling, components;
        
        // assert n in (-1, 0, 1), repr(n)
        
        if (n == 0) { ^this };
        
        sibling = this.prSibling(n);
        
        if (sibling.isNil) { ^nil };
        
        if (n == 1) {
            components = sibling.prGetDescendantsStartingWith;
        } {
            components = sibling.prGetDescendantsStoppingWith;
        };
        
        components.do { |component|
            if (component.isKindOf(FoscLeaf)) {
                if (FoscSelection([this, component]).areLogicalVoice) {
                    ^component;
                };
            };
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • prProcessContributionPacket
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    prProcessContributionPacket { |contributionPacket|
        var result, manager, indent, contributor, contributions;
        manager = FoscLilyPondFormatManager;
        indent = manager.indent;
        result = "";
        contributionPacket.do { |each|
            # contributor, contributions = each;
            if (contributions.notEmpty) {
                if (contributor.isSequenceableCollection) {
                    contributor = indent ++ contributor[0] ++ ":\n";
                } {
                    contributor = indent ++ contributor ++ ":\n";
                };
                result = result ++ contributor;
                contributions.do { |contribution|
                    contribution = indent.ditto(2) ++ contribution  ++ "\n";
                    result = result ++ contribution;
                };
            };
        };
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prRecurse
    !!!TODO: Can this be removed?
    -------------------------------------------------------------------------------------------------------- */
    prRecurse { |pulseDuration|
       writtenDuration = FoscDuration(pulseDuration);
    }   
    /* --------------------------------------------------------------------------------------------------------
    • prScale

    a = FoscNote(60, 1/4);
    a.prScale(1.5);
    a.prGetDuration.str;
    -------------------------------------------------------------------------------------------------------- */
    prScale { |multiplier|
        var newDuration;
        multiplier = FoscMultiplier(multiplier);
        newDuration = multiplier * this.prGetDuration;
        this.prSetDuration(newDuration);
    }
    /* --------------------------------------------------------------------------------------------------------
    • prSetDuration
    
    •
    a = FoscNote(60, 1/4, multiplier: 5);
    b = a.prSetDuration(3/16);
    b = FoscStaff(b);
    b.format;


    •
    a = FoscNote(60, 1/4);
    b = a.prSetDuration(3/16);
    b = FoscStaff(b);
    b.format;


    •
    b = FoscStaff([a = FoscNote(60, 1/4)]);
    a.prSetDuration(3/16);
    b.format;


    • without parent
    a = FoscNote(60, 1/4);
    b = a.prSetDuration(5/16);
    b = FoscStaff(b);
    b.show;


    • with parent
    b = FoscStaff([a = FoscNote(60, 1/4)]);
    a.prSetDuration(5/16);
    b.show;


    • without parent - !!!TODO: not yet working
    a = FoscNote(60, 1/4);
    b = a.prSetDuration(2/3);
    b = FoscStaff(b);
    b.show;

    • with parent - !!!TODO: not yet working
    b = FoscStaff([a = FoscNote(60, 1/4)]);
    a.prSetDuration(2/3);
    b.show;
    -------------------------------------------------------------------------------------------------------- */
    prSetDuration { |newDuration, repeatTies=false|
        var maker, components, newLeaves, followingLeafCount, followingLeaves, allLeaves, logicalTie;
        var logicalTieLeaves, index, next, tuplet;
        
        newDuration = FoscDuration(newDuration);
        
        if (multiplier.notNil) {
            this.multiplier_(newDuration / writtenDuration);
            ^FoscSelection(this);
        };

        try {
            this.writtenDuration_(newDuration);
            ^FoscSelection(this);
        };

        maker = FoscLeafMaker(repeatTies: repeatTies); 
        components = maker.([60], [newDuration]);

        newLeaves = FoscSelection(components).leaves;
        followingLeafCount = newLeaves.size - 1;
        followingLeaves = this ! followingLeafCount; 
        allLeaves = [this] ++ followingLeaves; 
        allLeaves.do { |leaf, i| leaf.writtenDuration_(newLeaves[i].writtenDuration) }; 

        logicalTie = this.prGetLogicalTie;
        logicalTieLeaves = all(FoscIteration(logicalTie).leaves);
        logicalTie.do { |leaf|
            leaf.detach(FoscTie);
            leaf.detach(FoscRepeatTie);
        };

        if (parent.notNil) {
            index = parent.indexOf(this);
            parent.insert(index + 1, followingLeaves);
        };

        index = logicalTieLeaves.indexOf(this);
        next = index + 1;
        logicalTieLeaves = logicalTieLeaves.prSetItem((next..next), followingLeaves);
        
        if (
            1 < logicalTieLeaves.size
            && { [FoscNote, FoscChord].any { |type| this.isKindOf(type) } }
        ) {
            FoscSelection(logicalTieLeaves).prAttachTieToLeaves;
        };

        if (components[0].isKindOf(FoscLeaf)) {
            ^FoscSelection(allLeaves);
        } {
            assert(components[0].isKindOf(FoscTuplet));
            assert(components.size == 1);
            tuplet = components[0];
            tuplet = FoscTuplet(tuplet.multiplier, []);
            mutate(allLeaves).wrap(tuplet);
            ^FoscSelection(tuplet);
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • prSplitByDurations


    • Example 1
    
    a = FoscNote(60, 4/4);
    b = a.prSplitByDurations([1/16, 3/16], tieSplitNotes: false);
    FoscStaff(b).show;


    • Example 2

    a = FoscNote(60, 4/4);
    b = a.prSplitByDurations([1/16, 3/16], isCyclic: true, tieSplitNotes: false);
    FoscStaff(b).show;


    • Example 3

    a = FoscNote(60, 4/4);
    b = a.prSplitByDurations([1/16, 3/16], isCyclic: true, tieSplitNotes: true);
    FoscStaff(b).show;


    • Example 4

    a = FoscNote(60, 4/4);
    b = a.prSplitByDurations([3/16, 5/32], isCyclic: true, tieSplitNotes: false);
    FoscStaff(b).show;


    • Example 5

    a = FoscStaff({ |i| FoscNote(60 + i, 3/16) } ! 4);
    a[0].prSplitByDurations([3/32], tieSplitNotes: false);
    a.selectLeaves.beam;
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    prSplitByDurations { |durations, isCyclic=false, tieSplitNotes=true, repeatTies=false|
        var leafDuration, lastDuration, originallyTied, originallyRepeatTied, resultSelections;
        var graceContainer, afterGraceContainer, leafProlation, newLeaf, preProlatedDuration, selection;
        var resultComponents, resultLeaves, firstResultLeaf, lastResultLeaf, direction, container, tie;

        durations = durations.collect { |each| FoscDuration(each) };
        leafDuration = FoscInspection(this).duration;
        if (isCyclic) { durations = durations.repeatToAbsSum(leafDuration) };
        if (durations.sum < leafDuration) {
            lastDuration = leafDuration - durations.sum;
            durations = durations.add(lastDuration);
        };
        durations = durations.truncateToSum(leafDuration);
        originallyTied = this.prHasIndicator(FoscTie);
        originallyRepeatTied = this.prHasIndicator(FoscRepeatTie);
        resultSelections = [];
        graceContainer = this.prDetachGraceContainer;
        afterGraceContainer = this.prDetachAfterGraceContainer;
        leafProlation = this.prGetParentage.prolation;

        durations.do { |duration|
            newLeaf = this.copy;
            preProlatedDuration = duration / leafProlation;
            selection = newLeaf.prSetDuration(preProlatedDuration, repeatTies: repeatTies);
            resultSelections = resultSelections.add(selection);
        };

        resultComponents = FoscSelection(resultSelections).flat;
        resultLeaves = resultComponents.leaves(graceNotes: false);
        assert(resultSelections.every { |each| each.isKindOf(FoscSelection) });
        assert(resultComponents.every { |each| each.isKindOf(FoscComponent) });
        assert(resultLeaves.areLeaves);
        resultLeaves.do { |leaf| leaf.detach(Object) };

        if (parent.notNil) { mutate(this).replace(resultComponents) };
        firstResultLeaf = resultLeaves[0];
        lastResultLeaf = resultLeaves.last;

        FoscInspection(this).indicators.do { |indicator|
            this.detach(indicator);
            if (indicator.respondsTo('timeOrientation')) { direction = indicator.timeOrientation };
            direction = direction ? 'left';
            switch(direction,
                'left', { firstResultLeaf.attach(indicator); },
                'right', { lastResultLeaf.attach(indicator) },
                {
                    ^throw("%:%: indicator direction must be left or right: %"
                          .format(this.species, thisMethod.name, direction));
                };
            );
        };

        if (graceContainer.notNil) {
            container = graceContainer[0];
            assert(container.isKindOf(FoscGraceContainer));
            firstResultLeaf.attach(container);
        };
        if (afterGraceContainer.notNil) {
            container = afterGraceContainer[0];
            assert(container.isKindOf(FoscAfterGraceContainer));
            lastResultLeaf.attach(container);
        };
        if (resultComponents[0].isKindOf(FoscTuplet)) {
            mutate(resultComponents).fuse;
        };

        if (
            tieSplitNotes
            && { this.isKindOf(FoscNote) || { this.isKindOf(FoscChord) } }
        ) {
            resultLeaves.prAttachTieToLeaves(repeatTies: repeatTies);
        };  

        resultLeaves[0].detach(FoscRepeatTie);
        resultLeaves.last.detach(FoscTie); 
        if (originallyRepeatTied) {
            tie = FoscRepeatTie();
            resultLeaves[0].attach(tie);
        };
        if (originallyTied) {
            tie = FoscTie();
            resultLeaves.last.attach(tie);
        };
        assert(resultLeaves.isKindOf(FoscSelection));
        assert(resultLeaves.every { |each| each.isKindOf(FoscLeaf) });
        ^resultLeaves;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prToTupletWithRatio
    -------------------------------------------------------------------------------------------------------- */
    prToTupletWithRatio {
        ^this.notYetImplemented(thisMethod);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • writtenDuration

    Written duration of leaf.

    Returns duration.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • writtenDuration_

    Set written duration of leaf.
    -------------------------------------------------------------------------------------------------------- */
    writtenDuration_ { |duration|
        var rational;
        rational = FoscDuration(duration);
        if (rational.isAssignable.not) {
            ^throw("%::writtenDuration_: not assignable duration: %".format(this.species, rational.str));
        };
        writtenDuration = rational;
    }
}
