/*******************************************************************************
 * Copyright (c) 2013-2017 Lorenzo Bettini.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Lorenzo Bettini - Initial contribution and API
 *******************************************************************************/

package org.eclipse.xsemantics.example.fjcached.performance

import com.google.inject.Inject
import org.eclipse.xsemantics.example.fj.fj.Program
import org.eclipse.xsemantics.example.fj.tests.FJInjectorProvider
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import org.junit.runner.RunWith

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(FJInjectorProvider))
class FjPerformanceTest extends AbstractFjPerformanceTest {

	@Inject extension ParseHelper<Program>

	@Inject extension ValidationTestHelper

	override validateInput(CharSequence input) {
		input.parse.assertNoErrors
	}

}
