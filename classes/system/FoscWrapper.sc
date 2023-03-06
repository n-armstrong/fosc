/* ------------------------------------------------------------------------------------------------------------
• FoscWrapper

Indicator wrapper.
------------------------------------------------------------------------------------------------------------ */
FoscWrapper : Fosc {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <component, <context, deactivate, <effectiveContext, <indicator, <annotation, <syntheticOffset;
    *new { |component, context, deactivate=false, effectiveContext, indicator, annotation, syntheticOffset|
        ^super.new.init(component, context, deactivate, effectiveContext, indicator, annotation,
            syntheticOffset);
    }
    init { |argComponent, argContext, argDeactivate, argEffectiveContext, argIndicator, argAnnotation,
        argSyntheticOffset|
        var message;
        
        if (argAnnotation.notNil) {
            assert([Symbol, String].any { |type| argAnnotation.isKindOf(type) });
        };
        
        annotation = argAnnotation;
        
        if (argComponent.notNil) {
            assert([FoscComponent].any { |type| argComponent.isKindOf(type) });
        };
        
        component = argComponent;
        context = argContext;
        deactivate = argDeactivate;
        indicator = argIndicator;
        
        if (argSyntheticOffset.notNil) { syntheticOffset = FoscOffset(argSyntheticOffset) };
        if (component.notNil) { this.prBindComponent(component) };
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • annotation

    Gets indicator wrapper annotation.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • component
    
    Gets start component of indicator wrapper.

    Returns component.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • context

    Gets context of indicator wrapper.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • deactivate

    Is true when wrapper deactivates tag.
    -------------------------------------------------------------------------------------------------------- */
    deactivate {
        assert([true, false, nil].includes(deactivate));
        ^deactivate;
    }
    /* --------------------------------------------------------------------------------------------------------
    • deactivate_
    -------------------------------------------------------------------------------------------------------- */
    deactivate_ { |bool|
        assert(bool.isKindOf(Boolean));
        deactivate = bool;
    }
    /* --------------------------------------------------------------------------------------------------------
    • indicator
    
    Gets indicator of indicator wrapper.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • startOffset
    
    Gets start offset of indicator wrapper.

    This is either the wrapper's synthetic offset or the start offset of the wrapper's component.

    Returns offset.
    -------------------------------------------------------------------------------------------------------- */
    startOffset {
        if (syntheticOffset.notNil) { ^syntheticOffset };
        ^component.prGetTimespan.startOffset;
    }
    /* --------------------------------------------------------------------------------------------------------
    • syntheticOffset
    
    Gets synthetic offset of indicator wrapper.

    Returns offset or nil.
    -------------------------------------------------------------------------------------------------------- */
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • asCompileString
    -------------------------------------------------------------------------------------------------------- */
    asCompileString {
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • copy

    Copies indicator wrapper.
    -------------------------------------------------------------------------------------------------------- */
    copy {
        ^this.species.new(
            component: nil,
            context: context,
            annotation: annotation,
            deactivate: deactivate,
            indicator: indicator.copy,
            syntheticOffset: syntheticOffset
        );
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prBindComponent

    a = FoscNote(60, 1/4);
    b = FoscVoice([a]);
    a.attach(FoscMarkup("foo"));
    a.attach(FoscDynamic('p'));
    c = FoscScore([FoscStaff([b])]);
    a.attach(FoscTimeSignature([4,4]), context: "Score");
    a.wrappers.do { |each| [each.indicator, each.context].postln };
    c.lilypond;
    -------------------------------------------------------------------------------------------------------- */
    prBindComponent { |argComponent|
        if (indicator.respondsTo('context') && { indicator.context.notNil }) {
            this.prWarnDuplicateIndicator(component);
            this.prUnbindComponent;
            component = argComponent;
            this.prUpdateEffectiveContext;
            
            if (
                indicator.respondsTo('mutatesOffsetsInSeconds')
                && { indicator.mutatesOffsetsInSeconds.not }
            ) {
                component.prUpdateLater(offsetsInSeconds: true);
            };
        };

        component.wrappers.add(this);
    }
    /* --------------------------------------------------------------------------------------------------------
    • prBindEffectiveContext
    -------------------------------------------------------------------------------------------------------- */
    prBindEffectiveContext { |correctEffectiveContext|
        this.prUnbindEffectiveContext;
        
        if (correctEffectiveContext.notNil) {
            correctEffectiveContext.dependentWrappers.add(this);
        };
        
        effectiveContext = correctEffectiveContext;
        this.prUpdateEffectiveContext;
        
        if (
            indicator.respondsTo('mutatesOffsetsInSeconds')
            && { indicator.mutatesOffsetsInSeconds.not }
        ) {
            component.prUpdateLater(offsetsInSeconds: true);
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • prDetach
    -------------------------------------------------------------------------------------------------------- */
    prDetach {
        this.prUnbindComponent;
        this.prUnbindEffectiveContext;
        ^this;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prFindCorrectEffectiveContext
    -------------------------------------------------------------------------------------------------------- */
    prFindCorrectEffectiveContext {
        var localContext, candidate, parentage;

        if (context.isNil) { ^nil };

        localContext = context;
        parentage = component.prGetParentage;

        case
        { context.isKindOf(Class) } {
            parentage.do { |component|
                if (component.isKindOf(FoscContext)) {
                    block { |break|
                        if (component.isKindOf(localContext)) {
                            candidate = component;
                            break.value;
                        };
                    };
                };
            };
        }
        { [Symbol, String].any { |type| localContext.isKindOf(type) } } {
            parentage.do { |component|
                if (component.isKindOf(FoscContext)) {
                    block { |break|
                        if (
                            component.name.asSymbol == localContext.asSymbol
                            || { component.lilypondType.asSymbol == localContext.asSymbol }
                            || { candidate == component }
                        ) {
                            candidate = component;
                            break.value;
                        };
                    };
                };
            };
        } 
        {
            ^throw("%:%: must be context, symbol or string: %."
                .format(this.species, thisMethod.name, localContext));
        };  

        if (candidate.isKindOf(FoscVoice)) {
            parentage.reverseDo { |component|
                if (component.isKindOf(FoscVoice)) {
                    block { |break|
                        if (component.name.asSymbol == candidate.name.asSymbol) {
                            candidate = component;
                            break.value;
                        };
                    };
                };
            };
        };

        ^candidate;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetEffectiveContext
    -------------------------------------------------------------------------------------------------------- */
    prGetEffectiveContext {
        if (component.notNil) { component.prUpdateNow(indicators: true) };
        ^effectiveContext;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetFormatPieces
    -------------------------------------------------------------------------------------------------------- */
    prGetFormatPieces {
        var result, bundle, localContext, lilypondFormat;
        
        result = [];
        if (annotation.notNil) { ^result };
        
        if (indicator.respondsTo('prGetLilypondFormatBundle')) {
            bundle = indicator.prGetLilypondFormatBundle(component);
            ^bundle;
        };
        
        try {
            localContext = this.prGetEffectiveContext;
            lilypondFormat = indicator.prGetLilypondFormat(context: localContext);
        } {
            lilypondFormat = indicator.prGetLilypondFormat;
        };
        
        if (lilypondFormat.isString) { lilypondFormat = [lilypondFormat] };
        result = result.addAll(lilypondFormat);
        
        if (this.prGetEffectiveContext.notNil) { ^result };
        //!!!TODO: result = result.collect { |each| "\\%\\%\\% % \\%\\%\\%".format(each) };
        
        result = result.collect { |each| "%".format(each) };

        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prUnbindComponent
    -------------------------------------------------------------------------------------------------------- */
    prUnbindComponent {
        if (component.notNil && { component.wrappers.includes(this) }) { component.wrappers.remove(this) };
        component = nil;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prUnbindEffectiveContext
    -------------------------------------------------------------------------------------------------------- */
    prUnbindEffectiveContext {
        if (effectiveContext.notNil && { effectiveContext.dependentWrappers.includes(this) }) {
            effectiveContext.dependentWrappers.remove(this);
        };

        effectiveContext = nil;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prUpdateEffectiveContext

    a = FoscScoreSegment.read(Threads, 'A1');
    -------------------------------------------------------------------------------------------------------- */ 
    prUpdateEffectiveContext {
        var correctEffectiveContext;
        
        correctEffectiveContext = this.prFindCorrectEffectiveContext;
        
        if (effectiveContext != correctEffectiveContext) {
            this.prBindEffectiveContext(correctEffectiveContext);
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • prWarnDuplicateIndicator
    -------------------------------------------------------------------------------------------------------- */
    prWarnDuplicateIndicator { |component|
        var type, command, wrapper, myLeak, parentage, wrapperContext, context, message;

        if (deactivate == true) { ^this };
        
        type = indicator.species;
        
        if (indicator.respondsTo('command') && { indicator.command.notNil }) {
            command = indicator.command;
        };

        wrapper = FoscInspection(component).effectiveWrapper(type, attributes: (command: command));

        if (
            wrapper.isNil
            || { wrapper.context.isNil }
            || { wrapper.deactivate }
            || { wrapper.startOffset != this.startOffset }
        ) {
            ^nil;
        };

        if (indicator.respondsTo('leak') && { indicator.leak.notNil }) {
            myLeak = indicator.leak;
        };

        if (wrapper.indicator.respondsTo('leak') && { wrapper.indicator.leak != myLeak }) {
            ^this;
        };

        context = component.prGetParentage.firstInstanceOf(FoscContext);
        parentage = wrapper.component.prGetParentage;
        wrapperContext = parentage.firstInstanceOf(FoscContext);
        
        if (wrapper.indicator == this.indicator && { context != wrapperContext }) {
            ^nil;
        };

        message = "Can't attach % to % in % because % is already attached to "
            .format(indicator.cs, component.cs, context.name ? context, wrapper.indicator.cs);
        
        if (component == wrapper.component) {
            message = message ++ "the same leaf.";
        } {
            message = message ++ "% in %.".format(wrapper.component, wrapperContext.name);
        };
        
        ^throw(message);
    }
}
