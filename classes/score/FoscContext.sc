/* ------------------------------------------------------------------------------------------------------------
• FoscContext
------------------------------------------------------------------------------------------------------------ */
FoscContext : FoscContainer {
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
	classvar <lilypondTypes;
    var <lilypondType, <consistsCommands, <dependentWrappers, <removeCommands, <defaultLilypondType='Voice';
	*initClass {
        lilypondTypes = #[
            'Score',
            'StaffGroup',
            'ChoirStaff',
            'GrandStaff',
            'PianoStaff',
            'Staff',
            'RhythmicStaff',
            'TabStaff',
            'DrumStaff',
            'VaticanaStaff',
            'MensuralStaff',
            'Voice',
            'VaticanaVoice',
            'MensuralVoice',
            'Lyrics',
            'DrumVoice',
            'FiguredBass',
            'TabVoice',
            'CueVoice',
            'ChordNames'
        ];
    }
    *new { |components, lilypondType='Context', isSimultaneous, name|
		^super.new(components, isSimultaneous, name).initFoscContext(lilypondType);
	}
	initFoscContext { |argLilypondType, argPlaybackManager|
		lilypondType = argLilypondType;
		consistsCommands = List[];
        dependentWrappers = List[];
		removeCommands = List[];
	}  
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
 	/* --------------------------------------------------------------------------------------------------------
    • asCompileString

    !!!TODO: INCOMPLETE

    FoscVoice(lilypondType: 'VaticanaVoice', name: 'soprano').cs;
    -------------------------------------------------------------------------------------------------------- */
    asCompileString {
        ^"%(%)".format(this.species.name, this.storeArgs.join(", "));
    }
    /* --------------------------------------------------------------------------------------------------------
    • copy
    -------------------------------------------------------------------------------------------------------- */
    copy {
        var new;
        new = super.copy;
        new.instVarPut('consistsCommands', consistsCommands);
        new.instVarPut('removeCommands', removeCommands);
        ^new;
    }
    /* --------------------------------------------------------------------------------------------------------
    • storeArgs
    -------------------------------------------------------------------------------------------------------- */
    storeArgs {
        ^[[], lilypondType, isSimultaneous, name];   
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
     • prFormatClosingSlot
 	
    a = FoscVoice([FoscNote(60, 1/4)]);
    b = FoscLilyPondFormatManager.bundleFormatContributions(a);
    a.prFormatClosingSlot(b);
    -------------------------------------------------------------------------------------------------------- */
	prFormatClosingSlot { |bundle|
        var result;
        
        result = [];
        result = result.add(['indicators', bundle.closing.indicators]);
        result = result.add(['commands', bundle.closing.commands]);
        result = result.add(['comments', bundle.closing.comments]);
        
        ^this.prFormatSlotContributionsWithIndent(result);
    }
	/* --------------------------------------------------------------------------------------------------------
 	• prFormatConsistsCommands
 	
    a = FoscVoice([FoscNote(60, 1/4)]);
    a.consistsCommands.add('Horizontal_bracket_engraver');
    a.prFormatConsistsCommands;
    -------------------------------------------------------------------------------------------------------- */
    prFormatConsistsCommands {
        var result, string;
        
        result = [];
        
        consistsCommands.do { |engraver|
            string = "\\consists %".format(engraver);
            result = result.add(string);
        };
        
        ^result;
    }
	/* --------------------------------------------------------------------------------------------------------
 	• prFormatInvocation
    
    a = FoscVoice([FoscNote(60, 1/4)]);
    a.prFormatInvocation;

    a = FoscVoice([FoscNote(60, 1/4)], name: 'foo');
    a.prFormatInvocation;
 	-------------------------------------------------------------------------------------------------------- */
	prFormatInvocation {
		var string;
		
        if (name.notNil) {
			string = "\\context % = \"%\"".format(lilypondType, name);
		} {
            string = "\\new %".format(lilypondType);
		};
		
        ^string;
	}
    /* --------------------------------------------------------------------------------------------------------
 	• prFormatOpenBracketsSlot
    -------------------------------------------------------------------------------------------------------- */
    prFormatOpenBracketsSlot { |bundle|
        var indent, result, bracketsOpen, removeCommands, consistsCommands, overrides, settings;
        var contribution, contributions, identifierPair;
        
        indent = FoscLilyPondFormatManager.indent;
        result = [];
        bracketsOpen = if (this.isSimultaneous) { #["<<"] } { #["{"] };
        removeCommands = this.prFormatRemoveCommands;
        consistsCommands = this.prFormatConsistsCommands;
        overrides = bundle.grobOverrides;
        settings = bundle.contextSettings;
        
        if ([removeCommands, consistsCommands, overrides, settings].any { |item| item.notEmpty }) {
            contributions = [this.prFormatInvocation ++ " \\with {"];
            identifierPair = #['contextBrackets', 'open'];
            result = result.add([identifierPair, contributions]);
            
            contributions = removeCommands.collect { |each| indent ++ each };
            identifierPair = #['engraverRemovals', 'removeCommands'];
            result = result.add([identifierPair, contributions]);
            
            contributions = consistsCommands.collect { |each| indent ++ each };
            identifierPair = #['engraverConsists', 'consistsCommands'];
            result = result.add([identifierPair, contributions]);
            
            contributions = overrides.collect { |each| indent ++ each };
            identifierPair = #['overrides', 'overrides'];
            result = result.add([identifierPair, contributions]);
            
            contributions = settings.collect { |each| indent ++ each };
            identifierPair = #['settings', 'settings'];
            result = result.add([identifierPair, contributions]);
            
            contributions = ["} %".format(bracketsOpen[0])];
            identifierPair = #['contextBrackets', 'open'];
            result = result.add([identifierPair, contributions]);
        } {
            contribution = this.prFormatInvocation;
            contribution = contribution ++ " %".format(bracketsOpen[0]);
            contributions = [contribution];
            identifierPair = #['contextBrackets', 'open'];
            result = result.add([identifierPair, contributions]);
        };

        ^result;
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
        
        ^this.prFormatSlotContributionsWithIndent(result);
	}
	/* --------------------------------------------------------------------------------------------------------
 	• prFormatRemoveCommands
 	
    a = FoscVoice([FoscNote(60, 1/4)]);
    a.removeCommands.add('Horizontal_bracket_engraver');
    a.prFormatRemoveCommands;
    -------------------------------------------------------------------------------------------------------- */
	prFormatRemoveCommands {
		var result, string;
        
        result = [];
		
        removeCommands.as(Array).sort.do { |engraver|
            string = "\\remove %".format(engraver);
            result = result.add(string);
        };
		
        ^result;
	}
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
 	• prGetLilypondFormat
 	
    a = FoscVoice([FoscNote(60, 1/4)]);
    a.prGetLilypondFormat;
    -------------------------------------------------------------------------------------------------------- */
 	prGetLilypondFormat {
 		this.prUpdateNow(indicators: true);
        ^this.prFormatComponent;
 	}
    /* --------------------------------------------------------------------------------------------------------
    • prGetPersistentWrappers
    -------------------------------------------------------------------------------------------------------- */
    prGetPersistentWrappers {
        var wrappers, indicator, key;
        
        this.prUpdateNow(indicators: true);    
        
        dependentWrappers.do { |wrapper|
            if (this.respondsTo('persistent') && { this.persistent }) { 
                
                case
                { this.respondsTo('parameter') } {
                    key = indicator.parameter;
                }
                { indicator.isKindOf(FoscInstrument) } {
                    key = 'Instrument';
                }
                {
                    key = indicator.species.asSymbol;
                };
                
                if (wrappers[key].isNil && { wrappers[key].startOffset <= wrapper[key].startOffset }) {
                    wrappers[key] = wrapper;
                };
            };
        };

        ^wrappers;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • consistsCommands

    Set of LilyPond engravers to include in context definition.

    Returns array.

    a = FoscStaff([]);
    a.consistsCommands.add('Horizontal_bracket_engraver');
    a.consistsCommands;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • lilypondType

    Gets and sets context name of context.
    
    Returns string.
    
    a = FoscStaff([]);
    a.lilypondType;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • lilypondType_
    -------------------------------------------------------------------------------------------------------- */
    lilypondType_ { |name|
        if (name.isNil) { name = this.name };
        lilypondType = name;
    }
    /* --------------------------------------------------------------------------------------------------------
    • lilypondContext

    Gets LilyPondContext associated with context.
    
    Returns FoscLilyPondContext instance.
    
    a = FoscStaff([]);
    a.lilypondContext.name;
    -------------------------------------------------------------------------------------------------------- */
    lilypondContext {
        var lilypondContext;
        
        try {
            lilypondContext = FoscLilyPondContext(lilypondType);
        } {
            lilypondContext = FoscLilyPondContext(this.defaultlilypondType);
        };
        
        ^lilypondContext;
    }
    /* --------------------------------------------------------------------------------------------------------
    • removeCommands

    Set of LilyPond engravers to remove from context.

    Returns set.
    
    a = FoscStaff();
    a.removeCommands.add('Horizontal_bracket_engraver');
    a.removeCommands;
    -------------------------------------------------------------------------------------------------------- */
}
