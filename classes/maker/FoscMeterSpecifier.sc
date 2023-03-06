/* ------------------------------------------------------------------------------------------------------------
• FoscMeterSpecifier

!!!TODO: can be deprecated. Use instead: mutate(music).rewriteMeters();



Meter specifier.


• Example 1

m = #[[2,4],[2,4],[2,4],[2,4]];
a = FoscLeafMaker().(#[60,62,64,65], [3/8,6/8,2/8,5/8]);
a = FoscMeterSpecifier(m).([a]);
a.selectLeaves[0].attach(FoscTimeSignature(#[2,4]));
FoscStaff(a).show;


• Example 2

m = #[[2,4],[2,4],[2,4],[2,4]];
a = FoscLeafMaker().(#[nil,62,nil,nil], [3/8,6/8,2/8,5/8]);
a = FoscMeterSpecifier(m).([a]);
t = nil;
a.do { |sel, i|
    if (m[i] != t) { sel.leafAt(0).attach(FoscTimeSignature(m[i])); t = m[i] };
};
FoscStaff(a).show;


• Example 3

m = #[[4,4],[4,4]];
a = FoscLeafMaker().(#[nil], [3/8,6/8,2/8,5/8]);
a = FoscMeterSpecifier(m).([a]);
t = nil;
a.do { |sel, i|
    if (m[i] != t) { sel.leafAt(0).attach(FoscTimeSignature(m[i])); t = m[i] };
};
FoscStaff(a).show;


• Example 4

t = #[[4,4],[1,4],[3,4]];
a = FoscTupletMaker();
m = a.(divisions: 1/4 ! 8, tupletRatios: #[[1,1,1,1,1]], applySpecifiers: false);
m = FoscFuseMask(#[17,20,-3]).(m);
m = FoscTupletSpecifier(extractTrivial: true, rewriteSustained: true, rewriteRestFilled: true).(m);
FoscBeamSpecifier().(m);
m = FoscMeterSpecifier(t).(m);
m.do { |sel, i| sel.leafAt(0).attach(FoscTimeSignature(t[i])) };
FoscLilyPondFile.rhythm(m).show;


• Example 5

Raise exception if duration of 'meters' is not equal to duration of 'selections'.

m = #[[5,4],[4,4]];
a = FoscLeafMaker().(#[60,62,64,65], [3/8,6/8,2/8,5/8]);
a = FoscMeterSpecifier(m).([a]);
------------------------------------------------------------------------------------------------------------ */
FoscMeterSpecifier : Fosc {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <meters, <attachTimeSignatures, <boundaryDepth, <maximumDotCount, <rewriteTuplets, <multimeasureRests;
    //var <publishStorageFormat=true;
    *new { |meters, attachTimeSignatures=false, boundaryDepth, maximumDotCount, rewriteTuplets=false,
        multimeasureRests=false|

        meters = meters.collect { |each| FoscMeter(each) };
        assert(attachTimeSignatures.isKindOf(Boolean));  
        assert(rewriteTuplets.isKindOf(Boolean));  
        assert(multimeasureRests.isKindOf(Boolean));  
        
        ^super.new.init(meters, attachTimeSignatures, boundaryDepth, maximumDotCount, rewriteTuplets,
            multimeasureRests);
    }
    init { |argMeters, argAttachTimeSignatures, argBoundaryDepth, argMaximumDotCount, argRewriteTuplets,
        argMultimeasureRests|    
        
        meters = argMeters;
        attachTimeSignatures = argAttachTimeSignatures;
        boundaryDepth = argBoundaryDepth;
        maximumDotCount = argMaximumDotCount;
        rewriteTuplets = argRewriteTuplets;
        multimeasureRests = argMultimeasureRests;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • asCompileString

    !!!TODO: NOT YET IMPLEMENTED

    Gets interpreter representation.
    
    Returns string.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • format

    !!!TODO: NOT YET IMPLEMENTED

    Formats duration spelling specifier.
    
    Returns string.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • value
    -------------------------------------------------------------------------------------------------------- */
    value { |selections|
        //if (selections.isKindOf(FoscSelection)) { selections = selections.items };
        if (selections.isKindOf(FoscSelection)) { selections = [selections] };

        selections = this.prRewriteMeter(selections);

        if (multimeasureRests) {
            selections = FoscMeterSpecifier.prRewriteRestFilled(selections, multimeasureRests: true);
        };

        //selections = FoscSelection(selections);
        ^selections;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prRewriteMeter

    m = #[[2,4],[4,4],[2,4]];
    a = FoscLeafMaker().(#[60,62,64,65], [3/8,6/8,2/8,5/8]);
    a = FoscMeterSpecifier(m, attachTimeSignatures: true).([a]);
    FoscStaff(a).show;
    -------------------------------------------------------------------------------------------------------- */
    prRewriteMeter { |selections|
        var durations, meterDuration, musicDuration, newSelections, staff, container, contents;
        var timeSignature, prevTimeSignature;

        durations = meters.collect { |each| FoscDuration(each) };
        meterDuration = durations.sum;
        musicDuration = selections.collect { |each| each.duration }.sum;
        
        if (meterDuration != musicDuration) {
            ^throw("%:%: duration of meters must be equal to duration of selections: meters: %, selections: %."
                .format(this.species, thisMethod.name, meterDuration.str, musicDuration.str));
        };
        
        newSelections = [];
        staff = FoscStaff();

        selections = FoscMeterSpecifier.prSplitAtMeasureBoundaries(selections, meters);

        selections.do { |selection|
            container = FoscContainer(selection);
            staff.add(container);
        };

        staff.do { |container, i|
            mutate(container[0..]).rewriteMeter(
                meter: meters[i],
                boundaryDepth: boundaryDepth,
                maximumDotCount: maximumDotCount,
                rewriteTuplets: rewriteTuplets
            );
        };

        staff.do { |container|
            contents = container[0..];
            contents.do { |component| component.prSetParent(nil) };
            newSelections = newSelections.add(contents);
        };

        if (this.attachTimeSignatures) {
            newSelections.do { |selection, i|
                timeSignature = FoscTimeSignature(meters[i]);
                if (timeSignature != prevTimeSignature) { selection.leafAt(0).attach(timeSignature) };
                prevTimeSignature = timeSignature;
            };
        };

        ^newSelections;
    }
    /* --------------------------------------------------------------------------------------------------------
    • *prRewriteRestFilled

    a = FoscLeafMaker().(#[nil,nil,nil,nil], [3/8,6/8,2/8,5/8]);
    a = FoscDurationSpecifier.prRewriteRestFilled([a], multimeasureRests: true);
    FoscStaff(a).show;
    -------------------------------------------------------------------------------------------------------- */
    *prRewriteRestFilled { |selections, multimeasureRests=false|
        var localSelections, maker, type, duration, multiplier, rest, rests;
        
        localSelections = [];
        maker = FoscLeafMaker();
        type = [FoscMultimeasureRest, FoscRest];
        
        selections.do { |selection|
            if (selection.every { |each| type.any { |type| each.isKindOf(type) }}.not) {
                localSelections = localSelections.add(selection);
            } {
                duration = selection.duration;
                if (multimeasureRests) {
                    multiplier = FoscMultiplier(duration);
                    rest = FoscMultimeasureRest(1);
                    rest.multiplier_(multiplier);
                    rests = FoscSelection([rest]);
                } {
                    rests = maker.([nil], [duration]);
                };
                localSelections = localSelections.add(rests);
            };
        };

        ^localSelections;
    }
    /* --------------------------------------------------------------------------------------------------------
    • *prSplitAtMeasureBoundaries
  
    
    • Example 1

    a = FoscLeafMaker().(#[60,62,64,65], [3/8,6/8,2/8,5/8]);
    //a.show;
    b = FoscMeterSpecifier.prSplitAtMeasureBoundaries([a], #[[4,4],[4,4]]);
    FoscStaff(b).show;


    • Example 2

    a = FoscLeafMaker().(#[60,62,64,65], [3/8,6/8,2/8,5/8]);
    //a.show;
    b = FoscMeterSpecifier.prSplitAtMeasureBoundaries(a, #[[2,4],[2,4],[2,4],[2,4]]);
    FoscStaff(b).show;

    FoscMutation

    l = Layer(divisions: 1!10, subdivisions: #[[1,1,1,1,1],[1,1,1]], tempo: 72);
    l.applySegmentation(#[9,11,11,9]);
    l.applyMasks(#[[-1,2,3]]);
    l.asFoscComponent;
    l.format;
    l.show;
    -------------------------------------------------------------------------------------------------------- */
    *prSplitAtMeasureBoundaries { |selections, meters|
        var durations, container, components, componentDurations, partSizes;
        
        durations = meters.collect { |each| FoscDuration(each) };
        container = FoscContainer(selections);
        mutate(container[0..]).split(durations: durations, tieSplitNotes: true);
        components = mutate(container).ejectContents;
        componentDurations = components.items.collect { |each| each.prGetDuration };
        partSizes = componentDurations.split(durations).collect { |each| each.size };
        selections = components.partitionBySizes(partSizes).items;
        
        ^selections;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • decreaseMonotonic

    Is true when all durations should be spelled as a tied series of monotonically decreasing values. Otherwise false.
    
    Defaults to true.
    
    Returns true or false.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • forbidMeterRewriting

    Is true when meter rewriting is forbidden.
    
    Defaults to nil.
    
    Returns boolean or nil.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • forbiddenDuration

    Gets forbidden written duration.
    
    Defaults to nil.
    
    Returns duration or nil.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • rewriteMeter

    Is true when all output divisions should rewrite meter. Otherwise false.
    
    Defaults to nil.
    
    Set to true, false or nil.
    
    Returns true, false or nil.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • spellMetrically

    Is true when durations should spell according to approximate common practice understandings of meter. Otherwise false.
    
    Spells unassignable durations like 5/16 and 9/4 metrically when set to 'unassignable'. Leaves other durations unchanged.
    
    Defaults to nil.
    
    Returns boolean, 'unassignable' or nil..
    -------------------------------------------------------------------------------------------------------- */
}
