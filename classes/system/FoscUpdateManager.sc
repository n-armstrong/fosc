/* --------------------------------------------------------------------------------------------------------
• FoscUpdateManager

Update manager.

Updates start offset, stop offsets and indicators everywhere in score.


• Example 1

a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
a.prUpdateNow(offsets: true);
a.doComponents { |each| [each, each.startOffset.str, each.stopOffset.str].postln };
-------------------------------------------------------------------------------------------------------- */
FoscUpdateManager : Fosc {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • *prGetAfterGraceNoteOffsets
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    *prGetAfterGraceNoteOffsets { |graceNote|
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • *prGetGraceNoteOffsets
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    *prGetGraceNoteOffsets { |graceNote|
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • *prGetScoreTreeStateFlags
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    *prGetScoreTreeStateFlags { |parentage|
        var offsetsAreCurrent=true, indicatorsAreCurrent=true, offsetsInSecondsAreCurrent=true;
        parentage.do { |component|
            if (offsetsAreCurrent) {
                if (component.offsetsAreCurrent.not) { offsetsAreCurrent = false };
            };
            if (indicatorsAreCurrent) {
                if (component.indicatorsAreCurrent.not) { indicatorsAreCurrent = false };
            };
            if (offsetsInSecondsAreCurrent) {
                if (component.offsetsInSecondsAreCurrent.not) { offsetsInSecondsAreCurrent = false };
            };
        };
        ^[offsetsAreCurrent, indicatorsAreCurrent, offsetsInSecondsAreCurrent];
    }
    /* --------------------------------------------------------------------------------------------------------
    • *prIterateEntireScore
    
    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    FoscUpdateManager.prIterateEntireScore(a);
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    *prIterateEntireScore { |scoreRoot|
        var components, graces;
        components = all(FoscIteration(scoreRoot).components(graceNotes: false));
        graces = all(FoscIteration(scoreRoot).components(type: FoscGraceContainer));
        components = components.addAll(graces);
        ^components;
    }
     /* --------------------------------------------------------------------------------------------------------
    • *prUpdateAllLeafIndicesAndMeasureNumbers

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65,67,69,71,72], [1/8]));
    a[0].attach(FoscTimeSignature(#[2,8]));
    a[4].attach(FoscTimeSignature(#[3,8]));
    a[7].attach(FoscTimeSignature(#[1,8]));
    // a.show;
    FoscUpdateManager.prUpdateAllLeafIndicesAndMeasureNumbers(a);
    a.doLeaves { |leaf| [leaf.str, leaf.leafIndex, leaf.measureNumber].postln };
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    *prUpdateAllLeafIndicesAndMeasureNumbers { |scoreRoot|
        var contexts;
        if (scoreRoot.isKindOf(FoscContext)) {
            scoreRoot.prUpdateMeasureNumbers;
            contexts = iterate(scoreRoot).components(FoscContext);
            contexts.do { |context|
                context.doLeaves { |leaf, i| leaf.instVarPut('leafIndex', i) };
            };
        } {
            scoreRoot.doLeaves { |leaf, i| leaf.instVarPut('leafIndex', i) };
        };
    } 
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prGetMeasureStartOffsets
    -------------------------------------------------------------------------------------------------------- */
    prGetMeasureStartOffsets { |component|
        var wrappers, type, scoreRoot, localWrappers, pairs, inspector, startOffset, timeSignature;
        var pair, offsetZero, defaultTimeSignature, defaultPair, parentage, scoreStopOffset, dummyLastPair;
        var currenStartOffset, currentTimeSignature, nextStartOffset, nextTimeSignature;
        var measureStartOffsets, measureStartOffset;

        wrappers = [];
        type = FoscTimeSignature;
        scoreRoot = component.prGetParentage.root;
        FoscUpdateManager.prIterateEntireScore(scoreRoot).do { |component|
            localWrappers = FoscInspection(component).wrappers(type);
            wrappers = wrappers.addAll(localWrappers);
        };

        pairs = [];
        wrappers.do { |wrapper|
            inspector = FoscInspection(wrapper.component);
            startOffset = inspector.timespan.startOffset;
            timeSignature = wrapper.indicator;
            pair = [startOffset, timeSignature];
            pairs = pairs.add(pair);
        };

        offsetZero = FoscOffset(0);
        defaultTimeSignature = FoscTimeSignature(#[4, 4]);
        defaultPair = [offsetZero, defaultTimeSignature];
        
        case
        { pairs.notEmpty && { pairs[0] != offsetZero } } {
            pairs = pairs.insert(0, defaultPair);
        }
        { pairs.isEmpty } {
            pairs = [defaultPair];
        };

        //pairs = pairs.sortN;
        parentage = component.prGetParentage;
        scoreRoot = parentage.root;
        inspector = FoscInspection(scoreRoot);
        scoreStopOffset = inspector.timespan.stopOffset;
        dummyLastPair = [scoreStopOffset, nil];
        pairs = pairs.add(dummyLastPair);
        measureStartOffsets = [];

        pairs.doAdjacentPairs { |currentPair, nextPair|
            # currenStartOffset, currentTimeSignature = currentPair;
            # nextStartOffset, nextTimeSignature = nextPair;
            measureStartOffset = currenStartOffset;
            while { measureStartOffset < nextStartOffset } {
                measureStartOffsets = measureStartOffsets.add(measureStartOffset);
                measureStartOffset = measureStartOffset + currentTimeSignature.duration;
            };
        };

        ^measureStartOffsets;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prMakeMetronomeMarkMap
    -------------------------------------------------------------------------------------------------------- */ 
    // abjad 3.0
    prMakeMetronomeMarkMap { |scoreRoot|
        var pairs, allStopOffsets, indicators, metronomeMark, pair, scoreStopOffset, timespans, clocktimeRate;
        var clocktimeStartOffset, startOffset, stopOffset, duration, multiplier, clocktimeDuration, timespan;

        pairs = [];
        allStopOffsets = Set[];

        FoscUpdateManager.prIterateEntireScore(scoreRoot).do { |component|
            indicators = component.prGetIndicators(FoscMetronomeMark);
            if (indicators.size == 1) {
                metronomeMark = indicators[0];
                if (metronomeMark.isImprecise.not) {
                    pair = [component.startOffset, metronomeMark];
                    pairs = pairs.add(pair);
                };
            };
            if (component.stopOffset.notNil) {
                allStopOffsets = allStopOffsets.add(component.stopOffset);
            };
        };

        pairs = pairs.sort { |a, b| a[0] < b[0] };
        if (pairs.isEmpty) { ^nil };
        if (pairs[0][0] != 0) { ^nil };
        scoreStopOffset = allStopOffsets.maxItem;
        timespans = FoscTimespanList();
        clocktimeRate = FoscMetronomeMark(#[1,4], 60);
        clocktimeStartOffset = FoscOffset(0);

        (pairs.add(pairs[0])).doAdjacentPairs { |left, right|
            metronomeMark = left.last;
            startOffset = left[0];
            stopOffset = right[0];
            if (stopOffset == 0) { stopOffset = scoreStopOffset };
            duration = stopOffset - startOffset;
            multiplier = FoscMultiplier(60, metronomeMark.unitsPerMinute);
            clocktimeDuration = duration / metronomeMark.referenceDuration;
            clocktimeDuration = clocktimeDuration * multiplier;
            timespan = FoscAnnotatedTimespan(
                startOffset: startOffset,
                stopOffset: stopOffset,
                annotation: [clocktimeStartOffset, clocktimeDuration]
            );

            timespans = timespans.add(timespan);
            clocktimeStartOffset = clocktimeStartOffset + clocktimeDuration;
        };

        ^timespans;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prToMeasureNumber

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65,67,69,71,72], [1/8]));
    set(a).autoBeaming = false;
    a[0].attach(FoscTimeSignature(#[2,8]));
    a[4].attach(FoscTimeSignature(#[3,8]));
    a[7].attach(FoscTimeSignature(#[1,8]));

    a.prUpdateMeasureNumbers;
    a.doLeaves { |leaf| [leaf.str, leaf.measureNumber].postln };
    -------------------------------------------------------------------------------------------------------- */
    prToMeasureNumber { |component, measureNumberStartOffsets|
        var inspector, componentStartOffset, pairs, measureNumber;
        inspector = FoscInspection(component);
        componentStartOffset = inspector.timespan.startOffset;
        measureNumberStartOffsets = measureNumberStartOffsets.add(inf);
        measureNumberStartOffsets.doAdjacentPairs { |a, b, i|
            if (a <= componentStartOffset && { componentStartOffset < b }) {
                measureNumber = i + 1;
                ^measureNumber;
            };
        };
        ^throw("can not find measure number: %, %.".format(component, measureNumberStartOffsets));
    }
    /* --------------------------------------------------------------------------------------------------------
    • prUpdateAllIndicators

    • Example1
    
    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a[0].attach(FoscDynamic('p'));
    a[0].prUpdateNow(indicators: true);
    a[0].prGetEffective(FoscDynamic);
    a[0].wrappers[0].indicator;
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    prUpdateAllIndicators { |scoreRoot|
        var components;
        components = FoscUpdateManager.prIterateEntireScore(scoreRoot);
        components.do { |component|
            component.wrappers.do { |wrapper|
                if (wrapper.context.notNil) { wrapper.prUpdateEffectiveContext };
            };
            component.instVarPut('indicatorsAreCurrent', true);
        };
    } 
    /* --------------------------------------------------------------------------------------------------------
    • prUpdateAllOffsets

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    FoscUpdateManager().prUpdateAllOffsets(a);
    a.doComponents { |each| [each, each.startOffset.str, each.stopOffset.str].postln };
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    prUpdateAllOffsets { |scoreRoot|
        FoscUpdateManager.prIterateEntireScore(scoreRoot).do { |component|
            this.prUpdateComponentOffsets(component);
            component.instVarPut('offsetsAreCurrent', true);
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • prUpdateAllOffsetsInSeconds
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    prUpdateAllOffsetsInSeconds { |scoreRoot|
        var timespans;
        this.prUpdateAllOffsets(scoreRoot);
        timespans = this.prMakeMetronomeMarkMap(scoreRoot);
        FoscUpdateManager.prIterateEntireScore(scoreRoot).do { |component|
            this.prUpdateClocktimeOffsets(component, timespans);
            component.instVarPut('offsetsInSecondsAreCurrent', false);
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • prUpdateClocktimeOffsets

    a = FoscStaff(FoscLeafMaker().(60 ! 12, [1/4]));
    b = FoscScore([a]);
    b.leafAt(0).attach(FoscMetronomeMark([1,4], 60));
    b.leafAt(6).attach(FoscMetronomeMark([1,4], 120));    
    FoscUpdateManager().prUpdateNow(b, offsetsInSeconds: true);
    
    b.prGetDurationInSeconds.asFloat;
    a.doLeaves  { |e| [e.startOffsetInSeconds.asFloat, e.prGetDurationInSeconds.asFloat].postln };
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    prUpdateClocktimeOffsets { |component, timespans|
        var pair, clocktimeStartOffset, clocktimeDuration, localOffset, multiplier, duration, offset;

        if (timespans.isNil) { ^this };

        timespans.do { |timespan|  
            if (
                timespan.startOffset <= component.startOffset
                && { component.startOffset < timespan.stopOffset }
           ) {
                pair = timespan.annotation;
                # clocktimeStartOffset, clocktimeDuration = pair;
                localOffset = component.startOffset - timespan.startOffset;
                multiplier = localOffset / timespan.duration;
                duration = multiplier * clocktimeDuration;
                offset = clocktimeStartOffset + duration;
                component.instVarPut('startOffsetInSeconds', FoscOffset(offset));
            };
            if (
                timespan.startOffset <= component.stopOffset
                && { component.stopOffset < timespan.stopOffset }
            ) {
                pair = timespan.annotation;
                # clocktimeStartOffset, clocktimeDuration = pair;
                localOffset = component.stopOffset - timespan.startOffset;
                multiplier = localOffset / timespan.duration;
                duration = multiplier * clocktimeDuration;
                offset = clocktimeStartOffset + duration;
                component.instVarPut('stopOffsetInSeconds', FoscOffset(offset));
                ^this;
            };
        };

        if (component.stopOffset == timespans.last.stopOffset) {
            pair = timespans.last.annotation;
            # clocktimeStartOffset, clocktimeDuration = pair;
            offset = clocktimeStartOffset + clocktimeDuration;
            component.instVarPut('stopOffsetInSeconds', FoscOffset(offset));
            ^this;
        };

        ^throw("%:%: can't find stop offset in %.".format(this.species, thisMethod.name, timespans));
    }
    /* --------------------------------------------------------------------------------------------------------
    • prUpdateComponentOffsets

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    FoscUpdateManager().prUpdateAllOffsets(a);
    a.doComponents { |each| [each, each.startOffset.str, each.stopOffset.str].postln };
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    prUpdateComponentOffsets { |component|
        var pair, startOffset, stopOffset, previous;
        case
        { component.parent.isKindOf(FoscGraceContainer) } {
            pair = FoscUpdateManager.prGetGraceNoteOffsets(component);
            # startOffset, stopOffset = pair;
        }
        { component.parent.isKindOf(FoscAfterGraceContainer) } {
            pair = FoscUpdateManager.prGetAfterGraceNoteOffsets(component);
            # startOffset, stopOffset = pair;
        } {
            previous = component.prSibling(-1);
            if (previous.notNil) {
                startOffset = previous.stopOffset;
            } {
                startOffset = FoscOffset(0);
            };
            stopOffset = startOffset + component.prGetDuration;
        };
        component.instVarPut('startOffset', startOffset);
        component.instVarPut('stopOffset', stopOffset);
        component.timespan.instVarPut('startOffset', startOffset);
        component.timespan.instVarPut('stopOffset', stopOffset);
    }
    /* --------------------------------------------------------------------------------------------------------
    • prUpdateMeasureNumbers

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65,67,69,71,72], [1/8]));
    a[0].attach(FoscTimeSignature(#[2,8]));
    a[4].attach(FoscTimeSignature(#[3,8]));
    a[7].attach(FoscTimeSignature(#[1,8]));
    a.show;
    
    a.prUpdateMeasureNumbers;
    a.doLeaves { |leaf| [leaf.str, leaf.measureNumber].postln };
    -------------------------------------------------------------------------------------------------------- */
    prUpdateMeasureNumbers { |component|
        var measureStartOffsets, scoreRoot, measureNumber;
        measureStartOffsets = this.prGetMeasureStartOffsets(component);
        assert(measureStartOffsets.notNil);
        scoreRoot = component.prGetParentage.root;
        FoscUpdateManager.prIterateEntireScore(scoreRoot).do { |component|
            measureNumber = this.prToMeasureNumber(component, measureStartOffsets);
            component.instVarPut('measureNumber', measureNumber);
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • prUpdateNow
   

    • Example 1

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a[0].prUpdateNow(offsets: true, indicators: true);
    a.doComponents { |each| [each, each.startOffset.str, each.stopOffset.str].postln };


    • Example 2
    
    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a[0].attach(FoscDynamic('p'));
    a[0].prUpdateNow(offsets: true, indicators: true);
    a[0].prGetEffective(FoscDynamic);
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    prUpdateNow { |component, offsets=false, offsetsInSeconds=false, indicators=false|
        var parentage, offsetsAreCurrent, indicatorsAreCurrent, offsetsInSecondsAreCurrent, scoreRoot;
        if (component.isForbiddenToUpdate) { ^this };
        parentage = component.prGetParentage(graceNotes: true);
        parentage.do { |parent|
            if (parent.isForbiddenToUpdate) { ^this };
        };
        # offsetsAreCurrent, indicatorsAreCurrent, offsetsInSecondsAreCurrent = 
                FoscUpdateManager.prGetScoreTreeStateFlags(parentage);
        scoreRoot = parentage.root;
        if (offsets && offsetsAreCurrent.not) {
            this.prUpdateAllOffsets(scoreRoot);
            FoscUpdateManager.prUpdateAllLeafIndicesAndMeasureNumbers(scoreRoot);
        };
        if (offsetsInSeconds && offsetsInSecondsAreCurrent.not) {
            this.prUpdateAllOffsetsInSeconds(scoreRoot);
        };
        if (indicators && indicatorsAreCurrent.not) {
            this.prUpdateAllIndicators(scoreRoot);
        };
    }
}
