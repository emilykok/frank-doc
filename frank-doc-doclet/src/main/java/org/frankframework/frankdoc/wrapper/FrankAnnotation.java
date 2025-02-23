/* 
Copyright 2021, 2022 WeAreFrank! 

Licensed under the Apache License, Version 2.0 (the "License"); 
you may not use this file except in compliance with the License. 
You may obtain a copy of the License at 

    http://www.apache.org/licenses/LICENSE-2.0 

Unless required by applicable law or agreed to in writing, software 
distributed under the License is distributed on an "AS IS" BASIS, 
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
See the License for the specific language governing permissions and 
limitations under the License. 
*/

package org.frankframework.frankdoc.wrapper;

/**
 * Models a Java 5 annotation. Only value types String[], String and Integer are supported.
 * @author martijn
 *
 */
public interface FrankAnnotation extends FrankProgramElement {
	/**
	 * Get the "value" field of the annotation.
	 * @throws FrankDocException
	 */
	Object getValue() throws FrankDocException;

	Object getValueOf(String fieldName) throws FrankDocException;

	FrankAnnotation getAnnotation(String name);
}
