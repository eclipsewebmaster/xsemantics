package it.xsemantics.dsl.tests;

import it.xsemantics.dsl.validation.IssueCodes;
import it.xsemantics.dsl.validation.XsemanticsJavaValidator;
import it.xsemantics.dsl.xsemantics.XsemanticsPackage;

import java.util.List;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.junit4.validation.AssertableDiagnostics;
import org.eclipse.xtext.junit4.validation.ValidationTestHelper;
import org.eclipse.xtext.junit4.validation.ValidatorTester;
import org.eclipse.xtext.xbase.XbasePackage;
import org.junit.Test;

public class XsemanticsValidatorTests extends XsemanticsAbstractTests {

	private static final String IN_SYSTEM_IT_XSEMANTICS_TEST_TYPE_SYSTEM = ", in system: it.xsemantics.test.TypeSystem";

	private XsemanticsJavaValidator validator;

	private ValidatorTester<XsemanticsJavaValidator> tester;

	private ValidationTestHelper validationTestHelper;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		validator = get(XsemanticsJavaValidator.class);
		validator.setEnableWarnings(false);
		tester = new ValidatorTester<XsemanticsJavaValidator>(validator,
				getInjector());
		validationTestHelper = get(ValidationTestHelper.class);
	}

	@Test
	public void testDuplicateJudgmentDescriptions() throws Exception {
		assertDuplicateErrors(
				loadModelAndValidate(testFiles
						.testJudgmentDescriptionsWithDuplicates()),
				"judgment", "type", IN_SYSTEM_IT_XSEMANTICS_TEST_TYPE_SYSTEM);
	}

	@Test
	public void testDuplicateJudgmentDescriptionSymbols() throws Exception {
		AssertableDiagnostics validate = loadModelAndValidate(testFiles
				.testJudgmentDescriptionsWithDuplicateSymbols());
		String messageFragment = "Duplicate JudgmentDescription symbols: |- :";
		validate.assertAll(AssertableDiagnostics.error(
				IssueCodes.DUPLICATE_JUDGMENT_DESCRIPTION_SYMBOLS,
				messageFragment), AssertableDiagnostics.error(
				IssueCodes.DUPLICATE_JUDGMENT_DESCRIPTION_SYMBOLS,
				messageFragment));
	}

	@Test
	public void testDuplicateRuleNames() throws Exception {
		assertDuplicateErrors(
				loadModelAndValidate(testFiles.testDuplicateRuleNames()),
				"rule", "Foo");
	}

	@Test
	public void testDuplicateCheckRuleNames() throws Exception {
		assertDuplicateErrors(
				loadModelAndValidate(testFiles.testDuplicateCheckRuleNames()),
				"checkrule", "Foo");
	}

	@Test
	public void testDuplicateRuleAndCheckRuleNames() throws Exception {
		AssertableDiagnostics validate = loadModelAndValidate(testFiles
				.testDuplicateRuleAndCheckRuleNames());
		validate.assertAll(AssertableDiagnostics
				.errorMsg("Duplicate checkrule with the same name"),
				AssertableDiagnostics
						.errorMsg("Duplicate rule with the same name"));
	}

	@Test
	public void testDuplicateParamNamesInRule() throws Exception {
		AssertableDiagnostics validate = loadModelAndValidate(testFiles
				.testDuplicateParamsInRule());
		assertContains(validate, "Duplicate parameter 'eClass'");
	}

	@Test
	public void testDuplicateParamNamesInJudgmentDescription() throws Exception {
		AssertableDiagnostics validate = loadModelAndValidate(testFiles
				.testDuplicateParamsInJudgmentDescription());
		assertContains(validate, "Duplicate parameter 'eClass'");
	}

	protected void assertContains(AssertableDiagnostics validate, String string) {
		final String diagnosticsToString = diagnosticsToString(validate);
		if (!diagnosticsToString.contains(string)) {
			fail("diagnostics: " + diagnosticsToString
					+ "\n does not contain: " + string);
		}
	}

	@Test
	public void testRulesWithExpressionInConclusion() throws Exception {
		assertOk(loadModelAndValidate(testFiles
				.testRuleWithExpressionInConclusion()));
	}

	@Test
	public void testRulesWithExpressionInConclusionWrong() throws Exception {
		AssertableDiagnostics validate = loadModelAndValidate(testFiles
				.testRuleWithExpressionInConclusion2());
		validate.assertAll(AssertableDiagnostics.error(
				IssueCodes.NOT_PARAMETER,
				"Must be a parameter, not an expression"));
	}

	@Test
	public void testRulesWithBlockExpressionInConclusion() throws Exception {
		assertOk(loadModelAndValidate(testFiles
				.testRuleWithBlockExpressionInConclusion()));
	}

	@Test
	public void testRulesWithSameEnvironmentNames() throws Exception {
		assertOk(loadModelAndValidate(testFiles
				.testRulesWithSameEnvironmentNames()));
	}

	@Test
	public void testRuleInvokingAnotherRule() throws Exception {
		assertOk(loadModelAndValidate(testFiles.testRuleInvokingAnotherRule()));
	}

	@Test
	public void testNoRuleForJudgmentDescription() throws Exception {
		validator.setEnableWarnings(true);
		AssertableDiagnostics validate = loadModelAndValidate(testFiles
				.testJudgmentDescriptions());
		validate.assertAll(AssertableDiagnostics
				.warningMsg("No rule defined for the judgment description"));
	}

	@Test
	public void testNoJudgmentDescriptionForRule() throws Exception {
		assertNoJudgmentDescription(testFiles
				.testRuleWithoutJudgmentDescription());
	}

	@Test
	public void testNoJudgmentDescriptionForRuleInvocation() throws Exception {
		assertNoJudgmentDescription(testFiles
				.testRuleInvocationWithoutJudgmentDescription());
	}

	protected void assertNoJudgmentDescription(CharSequence programString)
			throws Exception {
		AssertableDiagnostics validate = loadModelAndValidate(programString);
		String messageFragment = "No Judgment description for: ||- :";
		validate.assertAll(AssertableDiagnostics.error(
				IssueCodes.NO_JUDGMENT_DESCRIPTION, messageFragment));
	}

	@Test
	public void testRuleConclusionNotSubtype() throws Exception {
		EObject model = getModel(testFiles.testRuleWithConclusionNotSubtype()
				.toString());
		validationTestHelper
				.assertError(
						model,
						XsemanticsPackage.Literals.RULE_CONCLUSION_ELEMENT,
						IssueCodes.NOT_SUBTYPE,
						"Rule conclusion type EObject "
								+ "is not subtype of JudgmentDescription declared type "
								+ "EClass");
	}

	@Test
	public void testRuleInvocationNotSubtype() throws Exception {
		EObject model = getModel(testFiles
				.testRuleInvokingAnotherRuleNotValid().toString());
		validationTestHelper
				.assertError(
						model,
						XbasePackage.eINSTANCE.getXBinaryOperation(),
						IssueCodes.NOT_SUBTYPE,
						"Rule invocation type boolean "
								+ "is not subtype of JudgmentDescription declared type "
								+ "EObject");
	}

	@Test
	public void testRuleConclusionNotSubtypeBoth() throws Exception {
		AssertableDiagnostics validate = loadModelAndValidate(testFiles
				.testRuleWithConclusionNotSubtypeBoth());
		validate.assertAll(
				AssertableDiagnostics
						.error(IssueCodes.NOT_SUBTYPE,
								"Rule conclusion type EObject "
										+ "is not subtype of JudgmentDescription declared type "
										+ "EClass"),
				AssertableDiagnostics
						.error(IssueCodes.NOT_SUBTYPE,
								"Rule conclusion type String "
										+ "is not subtype of JudgmentDescription declared type "
										+ "EObject"));
	}

	@Test
	public void testFjExpressionTypingRules() throws Exception {
		assertOk(loadModelAndValidate(fjTSFiles.fjExpressionTypeRules()));
	}

	@Test
	public void testRulesOfTheSameKindWithSameArgumentTypes() throws Exception {
		AssertableDiagnostics validate = loadModelAndValidate(testFiles
				.testRulesOfTheSameKindWithSameArgumentTypes());
		validate.assertAll(
				AssertableDiagnostics
						.error(IssueCodes.DUPLICATE_RULE_WITH_SAME_ARGUMENTS,
								"Duplicate rule of the same kind with parameters: String, Integer"
										+ IN_SYSTEM_IT_XSEMANTICS_TEST_TYPE_SYSTEM),
				AssertableDiagnostics
						.error(IssueCodes.DUPLICATE_RULE_WITH_SAME_ARGUMENTS,
								"Duplicate rule of the same kind with parameters: String, int"
										+ IN_SYSTEM_IT_XSEMANTICS_TEST_TYPE_SYSTEM),
				AssertableDiagnostics
						.error(IssueCodes.DUPLICATE_RULE_WITH_SAME_ARGUMENTS,
								"Duplicate rule of the same kind with parameters: Object, Integer"
										+ IN_SYSTEM_IT_XSEMANTICS_TEST_TYPE_SYSTEM),
				AssertableDiagnostics
						.error(IssueCodes.DUPLICATE_RULE_WITH_SAME_ARGUMENTS,
								"Duplicate rule of the same kind with parameters: Object, Integer"
										+ IN_SYSTEM_IT_XSEMANTICS_TEST_TYPE_SYSTEM));
	}

	@Test
	public void testRulesOfTheSameKindWithSameInputArgumentTypes()
			throws Exception {
		AssertableDiagnostics validate = loadModelAndValidate(testFiles
				.testRulesOfTheSameKindWithSameInputArgumentTypes());
		validate.assertAll(AssertableDiagnostics.error(
				IssueCodes.DUPLICATE_RULE_WITH_SAME_ARGUMENTS,
				"Duplicate rule of the same kind with parameters: String"
						+ IN_SYSTEM_IT_XSEMANTICS_TEST_TYPE_SYSTEM),
				AssertableDiagnostics.error(
						IssueCodes.DUPLICATE_RULE_WITH_SAME_ARGUMENTS,
						"Duplicate rule of the same kind with parameters: String"
								+ IN_SYSTEM_IT_XSEMANTICS_TEST_TYPE_SYSTEM));
	}

	@Test
	public void testErrorSpecifications() throws Exception {
		assertOk(loadModelAndValidate(testFiles
				.testRuleWithErrorSpecifications()));
	}

	@Test
	public void testErrorSpecificationSourceNotEObject() throws Exception {
		AssertableDiagnostics validate = loadModelAndValidate(testFiles
				.testErrorSpecificationSourceNotEObject());
		validate.assertAll(AssertableDiagnostics.error(IssueCodes.NOT_EOBJECT,
				"Not an EObject: String"));
	}

	@Test
	public void testErrorSpecificationFeatureNotEStructuralFeature()
			throws Exception {
		AssertableDiagnostics validate = loadModelAndValidate(testFiles
				.testErrorSpecificationFeatureNotEStructuralFeature());
		validate.assertAll(AssertableDiagnostics.error(
				IssueCodes.NOT_ESTRUCTURALFEATURE,
				"Not an EStructuralFeature: EClass"));
	}

	@Test
	public void testRuleInvocationWithWrongOutputArg() throws Exception {
		EObject model = getModel(testFiles
				.testRuleInvocationWithWrongOutputArg().toString());
		validationTestHelper.assertError(model,
				XbasePackage.eINSTANCE.getXMemberFeatureCall(),
				IssueCodes.NOT_VALID_OUTPUT_ARG,
				"Not a valid argument for output parameter");
	}

	@Test
	public void testWrongVariableDeclarationAsOutputArgument() throws Exception {
		EObject model = getModel(testFiles
				.testWrongVariableDeclarationAsOutputArgument().toString());
		validationTestHelper.assertError(model,
				XbasePackage.eINSTANCE.getXVariableDeclaration(),
				IssueCodes.NOT_VALID_OUTPUT_ARG,
				"Not a valid argument for output parameter");
	}

	@Test
	public void testMoreThan3OutputParams() throws Exception {
		AssertableDiagnostics validate = loadModelAndValidate(testFiles
				.testJudgmentDescriptionsWith4OutputParams());
		validate.assertAll(AssertableDiagnostics.error(
				IssueCodes.TOO_MANY_OUTPUT_PARAMS,
				"No more than 3 output parameters are handled at the moment"));
	}

	@Test
	public void testNoInputParam() throws Exception {
		AssertableDiagnostics validate = loadModelAndValidate(testFiles
				.testJudgmentDescriptionsWithNoInputParam());
		validate.assertAll(AssertableDiagnostics.error(
				IssueCodes.NO_INPUT_PARAM,
				"No input parameter; at least one is needed"));
	}

	@Test
	public void testAssignmentToOutputParam() throws Exception {
		AssertableDiagnostics validate = loadModelAndValidate(testFiles
				.testRuleWithAssignmentToOutputParam());
		assertOk(validate);
	}

	@Test
	public void testAssignmentToInputParam() throws Exception {
		AssertableDiagnostics validate = loadModelAndValidate(testFiles
				.testRuleWithAssignmentToInputParam());
		validate.assertAll(AssertableDiagnostics.error(
				IssueCodes.ASSIGNMENT_TO_INPUT_PARAM,
				"Assignment to input parameter"));
	}

	@Test
	public void testRuleInvocationWithInputParamPassedAsOutput()
			throws Exception {
		EObject model = getModel(testFiles
				.testRuleInvocationWithInputParamPassedAsOutput().toString());
		validationTestHelper.assertError(model,
				XbasePackage.eINSTANCE.getXFeatureCall(),
				IssueCodes.NOT_VALID_OUTPUT_ARG,
				"Not a valid argument for output parameter");
	}

	@Test
	public void testVariableDeclarationAsOutputArgument() throws Exception {
		AssertableDiagnostics validate = loadModelAndValidate(testFiles
				.testVariableDeclarationAsOutputArgument());
		assertOk(validate);
	}

	@Test
	public void testWrongVariableDeclarationAsInputArgument() throws Exception {
		AssertableDiagnostics validate = loadModelAndValidate(testFiles
				.testWrongVariableDeclarationAsInputArgument());
		validate.assertAll(AssertableDiagnostics.error(
				IssueCodes.NOT_VALID_INPUT_ARG,
				"Not a valid argument for input parameter"));
	}

	@Test
	public void testRuleInvocationWithOutputArgInsideClosure() throws Exception {
		AssertableDiagnostics validate = loadModelAndValidate(testFiles
				.testRuleInvocationWithOutputArgInsideClosure());
		validate.assertAll(AssertableDiagnostics
				.error(org.eclipse.xtext.xbase.validation.IssueCodes.INVALID_MUTABLE_VARIABLE_ACCESS,
						"Cannot refer to a non-final variable eC from within a closure"));
	}

	@Test
	public void testAccessToVarInsideClosure() throws Exception {
		AssertableDiagnostics validate = loadModelAndValidate(testFiles
				.testAccessToVarInsideClosure());
		validate.assertAll(AssertableDiagnostics
				.error(org.eclipse.xtext.xbase.validation.IssueCodes.INVALID_MUTABLE_VARIABLE_ACCESS,
						"Cannot refer to a non-final variable s from within a closure"));
	}

	@Test
	public void testAccessToOutputParamInsideClosure() throws Exception {
		AssertableDiagnostics validate = loadModelAndValidate(testFiles
				.testAccessToOutputParamInsideClosure());
		validate.assertAll(AssertableDiagnostics
				.error(org.eclipse.xtext.xbase.validation.IssueCodes.INVALID_MUTABLE_VARIABLE_ACCESS,
						"Cannot refer to a non-final variable eC from within a closure"));
	}

	@Test
	public void testWrongReturnInPremises() throws Exception {
		AssertableDiagnostics validate = loadModelAndValidate(testFiles
				.testWrongReturnInPremises());
		validate.assertAll(
				AssertableDiagnostics.error(IssueCodes.RETURN_NOT_ALLOWED,
						"Return statements are not allowed here"),
				AssertableDiagnostics
						.error(org.eclipse.xtext.xbase.validation.IssueCodes.INCOMPATIBLE_TYPES,
								"Incompatible types"));
	}

	@Test
	public void testWrongThrowInPremises() throws Exception {
		AssertableDiagnostics validate = loadModelAndValidate(testFiles
				.testWrongThrowInPremises());
		validate.assertAll(AssertableDiagnostics.error(
				IssueCodes.THROW_NOT_ALLOWED,
				"Throw statements are not allowed here"));
		// AssertableDiagnostics
		// .error(org.eclipse.xtext.xbase.validation.IssueCodes.UNHANDLED_EXCEPTION,
		// "Unhandled exception"));
	}

	@Test
	public void testRuleWithBooleanExpressionsWithNoSideEffectInsideClosure()
			throws Exception {
		AssertableDiagnostics validate = loadModelAndValidate(testFiles
				.testForClosureWithExpressionWithNoSideEffect());
		assertOk(validate);
	}

	@Test
	public void testNoSideEffectButNoError() throws Exception {
		AssertableDiagnostics validate = loadModelAndValidate(testFiles
				.testNoSideEffectButNoError());
		assertOk(validate);
	}

	@Test
	public void testErrorNoSideEffect() throws Exception {
		AssertableDiagnostics validate = loadModelAndValidate(testFiles
				.testErrorNoSideEffect());
		assertOk(validate);
//		validate.assertAll(AssertableDiagnostics
//				.error(org.eclipse.xtext.xbase.validation.IssueCodes.INVALID_INNER_EXPRESSION,
//						"This expression is not allowed in this context"));
	}

	@Test
	public void testVarDeclInRuleInvokationShadowsPreviousVariable()
			throws Exception {
		AssertableDiagnostics validate = loadModelAndValidate(testFiles
				.testVarDeclInRuleInvokationShadowsPreviousVariable());
		validate.assertAll(AssertableDiagnostics
				.error(org.eclipse.xtext.xbase.validation.IssueCodes.VARIABLE_NAME_SHADOWING,
						"Duplicate variable name 's'"));
	}

	@Test
	public void testValidatorExtendsNotAbstractDeclarativeValidator()
			throws Exception {
		AssertableDiagnostics validate = loadModelAndValidate(testFiles
				.testSystemWithValidatorExtendsNotAbstractDeclarativeValidator());
		validate.assertAll(AssertableDiagnostics
				.error(IssueCodes.NOT_VALIDATOR,
						"Not an AbstractDeclarativeValidator: EClass"));
	}

	@Test
	public void testSystemExtendsInvalidBaseSystem() throws Exception {
		AssertableDiagnostics validate = loadModelAndValidate(testFiles
				.testSystemExtendsInvalidBaseSystem());
		validate.assertAll(AssertableDiagnostics
				.error(IssueCodes.NOT_VALID_SUPER_SYSTEM,
						"Not an Xsemantics system: TestInvalidBaseSystem"));
	}

	@Test
	public void testNoAuxFunForAuxiliaryDescription() throws Exception {
		validator.setEnableWarnings(true);
		AssertableDiagnostics validate = loadModelAndValidate(testFiles
				.testAuxiliaryDescriptions());
		validate.assertAll(
				AssertableDiagnostics
						.warningMsg("No function defined for the auxiliary description"),
				AssertableDiagnostics
						.warningMsg("No function defined for the auxiliary description"),
				AssertableDiagnostics
						.warningMsg("No function defined for the auxiliary description"));
	}

	protected AssertableDiagnostics loadModelAndValidate(
			CharSequence testFileContents) throws Exception {
		return tester.validate(getModel(testFileContents.toString()));
	}

	protected void assertDuplicateErrors(AssertableDiagnostics validate,
			String elementClassName, String duplicateName,
			String... msgFragments) {
		// System.out.println(diagnosticsToString(validate));
		String messageFragment = "Duplicate " + elementClassName + " '"
				+ duplicateName + "'";
		validate.assertAll(AssertableDiagnostics.errorMsg(messageFragment),
				AssertableDiagnostics.errorMsg(messageFragment));
		for (String msgFragment : msgFragments) {
			validate.assertAny(AssertableDiagnostics.errorMsg(msgFragment));
		}
	}

	protected void assertOk(AssertableDiagnostics validate) {
		if (listOfDiagnostics(validate).size() != 0) {
			for (Diagnostic diag : listOfDiagnostics(validate)) {
				System.err.println(diag.toString());
			}
			fail("There are expected to be no diagnostics.: "
					+ diagnosticsToString(validate));
		}
	}

	protected String diagnosticsToString(AssertableDiagnostics validate) {
		return listOfDiagnostics(validate).toString();
	}

	protected List<Diagnostic> listOfDiagnostics(AssertableDiagnostics validate) {
		return validate.getDiagnostic().getChildren();
	}
}
