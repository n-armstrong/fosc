/* ------------------------------------------------------------------------------------------------------------
• FoscDurationSpecifier

Duration specifier.
------------------------------------------------------------------------------------------------------------ */
FoscDurationSpecifier : Fosc {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <decreaseMonotonic, <forbidMeterRewriting, <forbiddenDuration, <rewriteMeter, <spellMetrically;
    var <publishStorageFormat=true;
    *new { |decreaseMonotonic=true, forbidMeterRewriting=false, forbiddenDuration, rewriteMeter=false,
        spellMetrically=false|
        
        assert(decreaseMonotonic.isKindOf(Boolean));
        assert(forbidMeterRewriting.isKindOf(Boolean));
        if (forbiddenDuration.notNil) {
            forbiddenDuration = FoscDuration(forbiddenDuration);
        };
        assert(rewriteMeter.isKindOf(Boolean));
        assert(spellMetrically.isKindOf(Boolean));
        
        ^super.new.init(decreaseMonotonic, forbidMeterRewriting, forbiddenDuration, rewriteMeter,
            spellMetrically);
    }
    init { |argDecreaseMonotonic, argForbidMeterRewriting, argForbiddenDuration, argRewriteMeter,
        argSpellMetrically|
        
        decreaseMonotonic = argDecreaseMonotonic;
        forbidMeterRewriting = argForbidMeterRewriting;
        forbiddenDuration = argForbiddenDuration;
        rewriteMeter = argRewriteMeter;
        spellMetrically = argSpellMetrically;
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


    • Example 1

    m = #[[2,4],[2,4],[2,4],[2,4]];
    a = FoscLeafMaker().(#[60,62,64,65], [3/8,6/8,2/8,5/8]);
    a = FoscDurationSpecifier(rewriteMeter: true).([a], meters: m);
    // attach time signatures where necessary
    t = nil;

    a.do { |sel, i|
        if (m[i] != t) {
            sel.leafAt(0).attach(FoscTimeSignature(m[i]));
            t = m[i];
        };
    };
    
    FoscStaff(a).show;


    • Example 2

    m = #[[2,4],[2,4],[2,4],[2,4]];
    a = FoscLeafMaker().(#[nil,62,nil,nil], [3/8,6/8,2/8,5/8]);
    a = FoscDurationSpecifier(rewriteMeter: true).([a], meters: m);
    // attach time signatures where necessary
    t = nil;
    a.do { |sel, i|
        if (m[i] != t) {
            sel.leafAt(0).attach(FoscTimeSignature(m[i]));
            t = m[i];
        };
    };
    FoscStaff(a).show;


    • Example 3

    m = #[[4,4],[4,4]];
    a = FoscLeafMaker().(#[nil,nil,nil,nil], [3/8,6/8,2/8,5/8]);
    a = FoscDurationSpecifier(rewriteMeter: true).([a], meters: m);
    // attach time signatures where necessary
    t = nil;
    a.do { |sel, i|
        if (m[i] != t) {
            sel.leafAt(0).attach(FoscTimeSignature(m[i]));
            t = m[i];
        };
    };
    FoscStaff(a).show;
    -------------------------------------------------------------------------------------------------------- */
    value { |selections, meters|
        // if (forbidMeterRewriting.not && rewriteMeter) {
        //     selections = FoscDurationSpecifier.prSplitAtMeasureBoundaries(selections, meters);
        // };
        if (forbidMeterRewriting.not && rewriteMeter) {
            selections = FoscDurationSpecifier.prRewriteMeter(selections, meters);
        };
        selections = FoscDurationSpecifier.prRewriteRestFilled(selections, multimeasureRests: true);
        ^selections;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • *prRewriteMeter

    a = FoscLeafMaker().(#[60,62,64,65], [3/8,6/8,2/8,5/8]);
    b = FoscDurationSpecifier.prRewriteMeter([a], [[4,4],[4,4]]);
    FoscSelection(b).show;

    a = FoscLeafMaker().(#[60,62,64,65], [3/8,6/8,2/8,5/8]);
    a = FoscDurationSpecifier.prRewriteMeter([a], [[3,8],[5,8],[3,8],[5,8]]);
    FoscSelection(a).show;



    a = FoscStaff(FoscLeafMaker().(#[60,60,62,62,64,64], [1/2,1/32,7/8,1/16,1/32,1/2]));
    m = a.selectLeaves;
    m.partitionBySizes(#[2,2,2]).do { |each| each.tie };
    a.leafAt(0).attach(FoscTimeSignature(#[2,4]));
    a.leafAt(1).attach(FoscTimeSignature(#[4,4]));
    a.leafAt(5).attach(FoscTimeSignature(#[2,4]));

    // use temporary containers to rewrite in-score selections of music
    n = m.partitionBySizes(#[1,3,2]);    
    n.do { |selection| mutate(selection).wrap(FoscContainer()) };
    mutate(a[1][0..]).rewriteMeter(FoscMeter(#[4,4]));
    x = a.prDelItem(1);
    x = x[0].prEjectContents;
    a.prSetItem((1..1), x);

    a.show;


    a = FoscLeafMaker().(#[60,62,64,65], [3/8,6/8,2/8,5/8]);
    b = FoscDurationSpecifier.prRewriteMeter([a], [[4,4],[4,4]]);
    FoscSelection(b).show;
    -------------------------------------------------------------------------------------------------------- */
    *prRewriteMeter { |selections, meters, rewriteTuplets=false|
        var durations, newSelections, meter, container, contents;

        meters = meters.collect { |each| FoscMeter(each) };
        durations = meters.collect { |each| FoscDuration(each) };
        selections = FoscDurationSpecifier.prSplitAtMeasureBoundaries(selections, meters);
        newSelections = [];

        selections.do { |selection, i|
            meter = meters[i];
            container = FoscContainer();
            mutate(selection).wrap(container);
            mutate(container[0..]).rewriteMeter(meter, rewriteTuplets: rewriteTuplets);
            contents = container[0..];
            contents.do { |component| component.prSetParent(nil) };
            newSelections = newSelections.add(contents);
        };

        ^newSelections;
    }
    // *prRewriteMeter { |selections, meters, referenceMeters, rewriteTuplets=false, repeatTies=false|
    //     var durations, maker, measures, staff, meter, contents;
    //     meters = meters.collect { |each| FoscMeter(each) };
    //     durations = meters.collect { |each| FoscDuration(each) };
    //     referenceMeters = referenceMeters ? [];
    //     selections = FoscDurationSpecifier.prSplitAtMeasureBoundaries(selections, meters);
    //     maker = FoscMeasureMaker();
    //     measures = maker.(durations);
    //     staff = FoscStaff(measures);
    //     mutate(staff).replaceMeasureContents(selections);
    //     staff.do { |measure, i|
    //         meter = meters[i];
    //         block { |break|
    //             referenceMeters.do { |referenceMeter|
    //                 if (referenceMeter.str == meter.str) {
    //                     meter = referenceMeter;
    //                     break.value;
    //                 };
    //             };
    //         };
    //         mutate(measure[0..]).rewriteMeter(meter, rewriteTuplets: rewriteTuplets, repeatTies: repeatTies);
    //     };
    //     selections = [];
    //     staff.do { |measure|
    //         contents = measure[0..];
    //         contents.do { |component| component.prSetParent(nil) };
    //         selections = selections.add(contents);
    //     };
    //     ^selections;
    // }
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

    !!!TODO: duplicated in FoscMeterSpecifier:prSplitAtMeasureBoundaries - REFACTOR
  
    
    • Example 1

    a = FoscLeafMaker().(#[60,62,64,65], [3/8,6/8,2/8,5/8]);
    //a.show;
    b = FoscDurationSpecifier.prSplitAtMeasureBoundaries([a], #[[4,4],[4,4]]);
    b.items;
    FoscStaff(b).show;


    • Example 2

    a = FoscLeafMaker().(#[60,62,64,65], [3/8,6/8,2/8,5/8]);
    //a.show;
    b = FoscDurationSpecifier.prSplitAtMeasureBoundaries(a, #[[2,4],[2,4],[2,4],[2,4]]);
    FoscStaff(b).show;


    • Example 3

    m = #[[2,4],[2,4],[2,4],[2,4]];
    a = FoscLeafMaker().(#[nil,62,nil,nil], [3/8,6/8,2/8,5/8]);
    a = FoscDurationSpecifier(rewriteMeter: true).([a], meters: m);
    FoscStaff(a).show;
    -------------------------------------------------------------------------------------------------------- */
    *prSplitAtMeasureBoundaries { |selections, meters|
        var durations, meterDuration, musicDuration, voice, components, componentDurations, partLengths;
        meters = meters.collect { |each| FoscMeter(each) };
        durations = meters.collect { |each| FoscDuration(each) };
        selections = FoscSelection(selections.flat).flat.items; // selections = array of selections as arg
        meterDuration = durations.sum;
        musicDuration = selections.collect { |each| FoscInspection(each).duration }.sum;
        if (meterDuration != musicDuration) {
            ^throw("%:%: duration of meters does not equal duration of selections: % - %."
                .format(this.species, thisMethod.name, meterDuration.str, musicDuration.str));
        };
        voice = FoscVoice(selections);
        mutate(voice[0..]).split(durations: durations, tieSplitNotes: true);
        components = mutate(voice).ejectContents;
        componentDurations = components.items.collect { |each| FoscInspection(each).duration };
        partLengths = componentDurations.split(durations).collect { |each| each.size };
        selections = components.partitionBySizes(partLengths);
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
