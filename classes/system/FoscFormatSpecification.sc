/* ------------------------------------------------------------------------------------------------------------
• FoscFormatSpecification

Specifies the storage format of a given object.
------------------------------------------------------------------------------------------------------------ */
FoscFormatSpecification : Fosc {
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// INIT
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/* --------------------------------------------------------------------------------------------------------
	•

	__slots__ = (
        '_client',
        '_coerce_for_equality',
        '_repr_args_values',
        '_repr_is_bracketed',
        '_repr_is_indented',
        '_repr_kwargs_names',
        '_repr_text',
        '_storage_format_args_values',
        '_storage_format_includes_root_package',
        '_storage_format_is_bracketed',
        '_storage_format_is_indented',
        '_storage_format_kwargs_names',
        '_storage_format_text',
        '_template_names',
        )

    ### INITIALIZER ###

    def __init__(
        self,
        client=None,
        coerce_for_equality=None,
        repr_args_values=None,
        repr_is_bracketed=None,
        repr_is_indented=None,
        repr_kwargs_names=None,
        repr_text=None,
        storage_format_args_values=None,
        storage_format_includes_root_package=None,
        storage_format_is_bracketed=None,
        storage_format_is_indented=True,
        storage_format_kwargs_names=None,
        storage_format_text=None,
        template_names=None,
        ):
        self._client = client
        self._coerce_for_equality = self._coerce_boolean(coerce_for_equality)
        self._repr_args_values = self._coerce_tuple(repr_args_values)
        self._repr_is_bracketed = self._coerce_boolean(repr_is_bracketed)
        self._repr_is_indented = self._coerce_boolean(repr_is_indented)
        self._repr_kwargs_names = self._coerce_tuple(repr_kwargs_names)
        self._repr_text = self._coerce_string(repr_text)
        self._storage_format_args_values = self._coerce_tuple(
            storage_format_args_values)
        self._storage_format_is_bracketed = self._coerce_boolean(
            storage_format_is_bracketed)
        self._storage_format_is_indented = self._coerce_boolean(
            storage_format_is_indented)
        self._storage_format_includes_root_package = self._coerce_boolean(
            storage_format_includes_root_package)
        self._storage_format_kwargs_names = self._coerce_tuple(
            storage_format_kwargs_names)
        self._storage_format_text = self._coerce_string(storage_format_text)
        self._template_names = self._coerce_tuple(template_names)
	-------------------------------------------------------------------------------------------------------- */
	var <client, <coerceForEquality, <reprArgsValues, <reprIsBracketed, <reprIsIndented, <reprKwargsNames;
	var <reprText, <storageFormatArgsValues, <storageFormatIncludesRootPackage, <storageFormatIsBracketed;
	var <storageFormatIsIndented, <storageFormatKwargsNames, <storageFormatText, <templateNames;
	*new { |client, coerceForEquality, reprArgsValues, reprIsBracketed, reprIsIndented, reprKwargsNames,
		reprText, storageFormatArgsValues, storageFormatIncludesRootPackage, storageFormatIsBracketed, storageFormatIsIndented, storageFormatKwargsNames, storageFormatText, templateNames|

		^super.new.init(client, coerceForEquality, reprArgsValues, reprIsBracketed, reprIsIndented, reprKwargsNames, reprText, storageFormatArgsValues, storageFormatIncludesRootPackage,
			storageFormatIsBracketed, storageFormatIsIndented, storageFormatKwargsNames, storageFormatText,
			templateNames);
	}
	init { |argClient, argCoerceForEquality, argReprArgsValues, argReprIsBracketed, argReprIsIndented,
		argReprKwargsNames, argReprText, argStorageFormatArgsValues, argStorageFormatIncludesRootPackage,
		argStorageFormatIsBracketed, argStorageFormatIsIndented, argStorageFormatKwargsNames,
		argStorageFormatText, argTemplateNames|

		client = argClient;
		coerceForEquality = this.prCoerceBoolean(argCoerceForEquality);
		reprArgsValues = argReprArgsValues;  // this.coerceTuple(argReprArgsValues);
		reprIsBracketed = this.prCoerceBoolean(argReprIsBracketed);
		reprIsIndented = this.prCoerceBoolean(argReprIsIndented);
		reprKwargsNames = argReprKwargsNames;  // this.coerceTuple(argReprKwargsNames);
		reprText = this.prCoerceString(argReprText);
		storageFormatArgsValues = argStorageFormatArgsValues;  // this.coerceTuple(argStorageFormatArgsValues);
		storageFormatIncludesRootPackage = this.prCoerceBoolean(argStorageFormatIncludesRootPackage);
		storageFormatIsBracketed = this.prCoerceBoolean(argStorageFormatIsBracketed);
		storageFormatIsIndented = this.prCoerceBoolean(argStorageFormatIsIndented) ? true;
		storageFormatKwargsNames = argStorageFormatKwargsNames; // this.coerceTuple(argStorageFormatKwargsNames);
		storageFormatText = this.prCoerceString(argStorageFormatText);
		templateNames = argTemplateNames; // this.coerceTuple(argTemplateNames);
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/* --------------------------------------------------------------------------------------------------------
	• prCoerceBoolean

	def _coerce_boolean(self, value):
        if value is not None:
            return bool(value)

   	a = FoscSlotContributions();
	a.prGetFormatSpecification;
	-------------------------------------------------------------------------------------------------------- */
	prCoerceBoolean { |expr|
		if (expr.notNil) { ^expr.booleanValue };
	}
    /* --------------------------------------------------------------------------------------------------------
    •
    def _coerce_string(self, value):
        if value is not None:
            return str(value)
    -------------------------------------------------------------------------------------------------------- */
    prCoerceString { |expr|
    	if (expr.notNil) { ^expr.asString };
    }
    /* --------------------------------------------------------------------------------------------------------
    •
    def _coerce_tuple(self, value):
        if value is not None:
            return tuple(value)
    -------------------------------------------------------------------------------------------------------- */
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    •
    @property
        def client(self):
            return self._client
    -------------------------------------------------------------------------------------------------------- */

    /* --------------------------------------------------------------------------------------------------------
    •
    @property
        def coerce_for_equality(self):
            return self._coerce_for_equality
    -------------------------------------------------------------------------------------------------------- */

    /* --------------------------------------------------------------------------------------------------------
    •
    @property
        def repr_args_values(self):
            return self._repr_args_values
    -------------------------------------------------------------------------------------------------------- */

    /* --------------------------------------------------------------------------------------------------------
    •
    @property
        def repr_is_bracketed(self):
            return self._repr_is_bracketed
    -------------------------------------------------------------------------------------------------------- */

    /* --------------------------------------------------------------------------------------------------------
    •
    @property
        def repr_is_indented(self):
            return self._repr_is_indented
    -------------------------------------------------------------------------------------------------------- */

    /* --------------------------------------------------------------------------------------------------------
    •
    @property
        def repr_kwargs_names(self):
            return self._repr_kwargs_names
    -------------------------------------------------------------------------------------------------------- */

    /* --------------------------------------------------------------------------------------------------------
    •
    @property
        def repr_text(self):
            return self._repr_text
    -------------------------------------------------------------------------------------------------------- */

    /* --------------------------------------------------------------------------------------------------------
    •
    @property
        def storage_format_args_values(self):
            return self._storage_format_args_values
    -------------------------------------------------------------------------------------------------------- */

    /* --------------------------------------------------------------------------------------------------------
    •
    @property
        def storage_format_includes_root_package(self):
            return self._storage_format_includes_root_package
    -------------------------------------------------------------------------------------------------------- */

    /* --------------------------------------------------------------------------------------------------------
    •
    @property
        def storage_format_is_bracketed(self):
            return self._storage_format_is_bracketed
    -------------------------------------------------------------------------------------------------------- */

    /* --------------------------------------------------------------------------------------------------------
    •
    @property
        def storage_format_is_indented(self):
            return self._storage_format_is_indented
    -------------------------------------------------------------------------------------------------------- */

    /* --------------------------------------------------------------------------------------------------------
    •
    @property
        def storage_format_kwargs_names(self):
            return self._storage_format_kwargs_names
    -------------------------------------------------------------------------------------------------------- */

    /* --------------------------------------------------------------------------------------------------------
    •
    @property
        def storage_format_text(self):
            return self._storage_format_text
    -------------------------------------------------------------------------------------------------------- */

    /* --------------------------------------------------------------------------------------------------------
    •
    @property
        def template_names(self):
            return self._template_names
    -------------------------------------------------------------------------------------------------------- */
}
