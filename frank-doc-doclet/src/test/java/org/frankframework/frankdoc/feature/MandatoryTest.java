package org.frankframework.frankdoc.feature;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;

import org.frankframework.frankdoc.wrapper.FrankClass;
import org.frankframework.frankdoc.wrapper.FrankClassRepository;
import org.frankframework.frankdoc.wrapper.FrankMethod;
import org.frankframework.frankdoc.wrapper.TestUtil;
import org.junit.Before;
import org.junit.Test;

public class MandatoryTest {
	private static final String PACKAGE = "org.frankframework.frankdoc.testtarget.featurepackage.";
	private static final String CLASS_NAME = PACKAGE + "ForMandatory";
	private FrankClass clazz;

	@Before
	public void setUp() throws Exception {
		FrankClassRepository classes = TestUtil.getFrankClassRepositoryDoclet(PACKAGE);
		clazz = classes.findClass(CLASS_NAME);
	}

	private FrankMethod findMethod(String name) {
		return Arrays.asList(clazz.getDeclaredMethods()).stream()
				.filter(m -> m.getName().equals(name))
				.findFirst()
				.get();
	}

	@Test
	public void whenNoAnnotationsThenNotMandatory() {
		FrankMethod m = findMethod("notMandatory");
		assertNull(Mandatory.getInstance().valueOf(m));		
	}

	@Test
	public void whenMethodUsesAnnotationWithIgnoreThenIgnore() {
		FrankMethod m = findMethod("mandatoryByAnnotationIgnore");
		assertEquals(Mandatory.Value.IGNORE_COMPATIBILITY, Mandatory.getInstance().valueOf(m));
	}

	@Test
	public void whenMethodUsesTagWithIgnoreThenIgnore() {
		FrankMethod m = findMethod("mandatoryByTagIgnore");
		assertEquals(Mandatory.Value.IGNORE_COMPATIBILITY, Mandatory.getInstance().valueOf(m));
	}

	@Test
	public void whenMethodUsesAnnotationWithoutIgnoreThenNoIgnore() {
		FrankMethod m = findMethod("mandatoryByAnnotationNoIgnore");
		assertEquals(Mandatory.Value.DONT_IGNORE_COMPATIBILITY, Mandatory.getInstance().valueOf(m));
	}

	@Test
	public void whenMethodUsesTagWithoutIgnoreThenNoIgnore() {
		FrankMethod m = findMethod("mandatoryByTagNoIgnore");
		assertEquals(Mandatory.Value.DONT_IGNORE_COMPATIBILITY, Mandatory.getInstance().valueOf(m));
	}
}
