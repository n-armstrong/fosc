/* ------------------------------------------------------------------------------------------------------------
• FoscScoreTemplate

!!!TODO:
- how are arguments to illustrate (staffSize, etc.) used in practice?

Abstract superclass for score templates.
------------------------------------------------------------------------------------------------------------ */
FoscScoreTemplate : Fosc {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <alwaysMakeGlobalRests=false, <doNotRequireMarginMarkup=false;
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC CLASS PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • attachDefaults

    Attaches defaults to all staff and staff group contexts in 'expr' when 'expr' is a score.

    Attaches defaults to 'expr' (without iterating 'expr') when 'expr' is a staff or staff group.

    Returns list of one wrapper for every indicator attached.
    -------------------------------------------------------------------------------------------------------- */
    attachDefaults { |expr|
        var wrappers, type, staves, leaf, clef, wrapper;

        wrappers = [];
        type = [FoscStaff, FoscStaffGroup];
        staves = expr.selectComponents(type);

        staves.do { |staff|
            leaf = staff.leafAt(0);

            if (leaf.notNil) {
                clef = leaf.prGetIndicator(FoscClef);
                
                if (clef.isNil) {
                    clef = FoscInspection(staff).annotation('defaultClef');
                    if (clef.notNil) { wrapper = leaf.attach(clef, wrapper: true) };
                    wrappers = wrappers.add(wrapper);
                };
            };
        };

        ^wrappers;
    }
    /* --------------------------------------------------------------------------------------------------------
    • illustrate
    -------------------------------------------------------------------------------------------------------- */
    illustrate { |paperSize, staffSize, includes|
        var score, lilypondFile;
        
        score = this.value;
        score.doComponents({ |voice| voice.add(FoscSkip(1)) }, FoscVoice);
        this.attachDefaults(score);
        lilypondFile = score.illustrate(paperSize, staffSize, includes);
        
        ^lilypondFile;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prMakeGlobalContext
    -------------------------------------------------------------------------------------------------------- */
    prMakeGlobalContext {
        var globalRests, globalSkips, globalContext;

        globalRests = FoscContext(lilypondType: 'GlobalRests', name: 'GlobalRests');
        globalSkips = FoscContext(lilypondType: 'GlobalSkips', name: 'GlobalSkips');
        globalContext = FoscContext(lilypondType: 'GlobalContext', name: 'GlobalContext');

        ^globalContext;
    }
}
/* ------------------------------------------------------------------------------------------------------------
• FoscGroupedRhythmicStavesScoreTemplate


• Example 1 - Create an empty score with one voice per staff

a = FoscGroupedRhythmicStavesScoreTemplate(staffCount: 2);
f = a.illustrate(staffSize: 12);
f.format;
f.show;


• Example 2 - With floating time signatures

a = FoscGroupedRhythmicStavesScoreTemplate(staffCount: 2, floatingTimeSignatures: true);
f = a.illustrate(staffSize: 12);
f.show;


• Example 3 - Add music to the empty score created from the template

a = FoscGroupedRhythmicStavesScoreTemplate(staffCount: 2);
b = a.value;
m = FoscLeafMaker().([60], (1/8) ! 8); // add music to 'v1'
b['v1'].add(m);
b.format;
f = b.illustrate(staffSize: 14);
f.show;


• Example 4 - More than one voice per staff

a = FoscGroupedRhythmicStavesScoreTemplate(staffCount: #[2,1,2]);
f = a.illustrate(staffSize: 12);
f.format;
------------------------------------------------------------------------------------------------------------ */
FoscGroupedRhythmicStavesScoreTemplate : FoscScoreTemplate {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <staffCount, <floatingTimeSignatures;
    *new { |staffCount=2, floatingTimeSignatures=false|
        ^super.new.init(staffCount, floatingTimeSignatures);
    }
    init { |argStaffCount, argFloatingTimeSignatures|
        staffCount = argStaffCount;
        floatingTimeSignatures = argFloatingTimeSignatures;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • value
    -------------------------------------------------------------------------------------------------------- */
    value {
        var staves, number, name, voice, staff, key, staffGroup, score, globalContext;

        staves = [];

        case
        { staffCount.isKindOf(Integer) } {
            staffCount.do { |i|
                number = i + 1;
                name = "v%".format(number);
                voice = FoscVoice([], name: name);
                name = "s%".format(number);
                staff = FoscStaff([voice], name: name, lilypondType: 'RhythmicStaff');
                staff.annotate('defaultClef', FoscClef('percussion'));
                staves = staves.add(staff);
            };
        }
        { staffCount.isSequenceableCollection } {
            staffCount.do { |size, i|
                number = i + 1;
                name = "s%".format(number);
                staff = FoscStaff(name: name, lilypondType: 'RhythmicStaff');
                staff.annotate('defaultClef', FoscClef('percussion'));

                size.do { |voiceCount|
                    if (size == 1) {
                        name = "v%".format(number);
                    } {
                        name = "v%_%".format(number, voiceCount + 1);
                        staff.isSimultaneous = true;
                    };

                    voice = FoscVoice(name: name);
                    staff.add(voice);
                };

                staves = staves.add(staff);
            };
        };

        staffGroup = FoscStaffGroup(staves, name: 'GroupedRhythmicStavesStaffGroup');
        score = FoscScore([staffGroup], name: 'GroupedRhythmicStavesScore');

        if (floatingTimeSignatures) {
            globalContext = this.prMakeGlobalContext;
            score.insert(0, globalContext);
        };

        ^score;
    }
}
/* ------------------------------------------------------------------------------------------------------------
• FoscGroupedStavesScoreTemplate


• Example 1 - Create an empty score with one voice per staff

a = FoscGroupedStavesScoreTemplate(staffCount: 2);
f = a.illustrate(staffSize: 12);
f.format;
f.show;


• Example 2 - Add music to the empty score created from the template

a = FoscGroupedStavesScoreTemplate(staffCount: 2);
b = a.value;
m = FoscLeafMaker().((20..72).mirror, [1/8]); // add music to 'v1'
b['v1'].add(m);
f = b.illustrate(staffSize: 14);
f.show;
------------------------------------------------------------------------------------------------------------ */
FoscGroupedStavesScoreTemplate : FoscGroupedRhythmicStavesScoreTemplate {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • value
    -------------------------------------------------------------------------------------------------------- */
    value {
        var staves, number, name, voice, staff, key, staffGroup, score, globalContext;

        staves = [];

        staffCount.do { |i|
            number = i + 1;
            name = "v%".format(number);
            voice = FoscVoice([], name: name);
            name = "s%".format(number);
            staff = FoscStaff([voice], name: name);
            staves = staves.add(staff);
        };

        staffGroup = FoscStaffGroup(staves, name: 'GroupedStavesStaffGroup');
        score = FoscScore([staffGroup], name: 'GroupedStavesScore');

        if (floatingTimeSignatures) {
            //!!!TODO: use a different stylesheet
            globalContext = this.prMakeGlobalContext;
            score.insert(0, globalContext);
        };

        ^score;
    }
}
/* ------------------------------------------------------------------------------------------------------------
• FoscGroupedStavesScoreTemplate


• Example 1 - Create an empty score with one voice per staff

a = FoscGroupedStavesScoreTemplate(staffCount: 2);
f = a.illustrate(staffSize: 12);
f.format;
f.show;


• Example 2 - Add music to the empty score created from the template

a = FoscGroupedStavesScoreTemplate(staffCount: 2);
b = a.value;
m = FoscLeafMaker().((20..72).mirror, [1/8]); // add music to 'v1'
b['v1'].add(m);
f = b.illustrate(staffSize: 14);
f.show;
------------------------------------------------------------------------------------------------------------ */
FoscStavesScoreTemplate : FoscGroupedStavesScoreTemplate {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • value
    -------------------------------------------------------------------------------------------------------- */
    value {
        var staves, number, name, voice, staff, key, staffGroup, score, globalContext;

        staves = [];

        staffCount.do { |i|
            number = i + 1;
            name = "v%".format(number);
            voice = FoscVoice([], name: name);
            name = "s%".format(number);
            staff = FoscStaff([voice], name: name);
            staves = staves.add(staff);
        };

        score = FoscScore(staves, name: 'GroupedStavesScore');

        if (floatingTimeSignatures) {
            //!!!TODO: use a different stylesheet
            globalContext = this.prMakeGlobalContext;
            score.insert(0, globalContext);
        };

        ^score;
    }
}
/* ------------------------------------------------------------------------------------------------------------
• FoscTwoStaffPianoScoreTemplate

!!!TODO: replace code in FoscScore:makePianoScore with this


• Example 1 - Add music to the empty score created from the template

a = FoscTwoStaffPianoScoreTemplate();
b = a.value;
m = FoscLeafMaker().((60..72).mirror, [1/8]); // add music to 'RHVoice'
b['RHVoice'].add(m);
f = b.illustrate(staffSize: 14);
f.show;
------------------------------------------------------------------------------------------------------------ */
FoscTwoStaffPianoScoreTemplate : FoscScoreTemplate {
    /* --------------------------------------------------------------------------------------------------------
    • value
    -------------------------------------------------------------------------------------------------------- */
    value {
        var rhVoice, rhStaff, lhVoice, lhStaff, staffGroup, score;

        rhVoice = FoscVoice(name: 'RHVoice');
        rhStaff = FoscStaff([rhVoice], name: 'RHStaff');

        lhVoice = FoscVoice(name: 'LHVoice');
        lhStaff = FoscStaff([lhVoice], name: 'LHStaff');
        lhStaff.annotate('defaultClef', FoscClef('bass'));

        staffGroup = FoscStaffGroup([rhStaff, lhStaff], name: 'PianoStaff');
        score = FoscScore([staffGroup], name: 'TwoStaffPianoScore');

        ^score;
    }
}
