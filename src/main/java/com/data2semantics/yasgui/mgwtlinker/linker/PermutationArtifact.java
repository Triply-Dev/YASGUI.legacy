package com.data2semantics.yasgui.mgwtlinker.linker;

/*
 * #%L
 * YASGUI
 * %%
 * Copyright (C) 2013 Laurens Rietveld
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import java.util.Set;

import com.data2semantics.yasgui.mgwtlinker.server.BindingProperty;
import com.google.gwt.core.ext.Linker;
import com.google.gwt.core.ext.linker.Artifact;
import com.google.gwt.core.ext.linker.Transferable;

@Transferable
public class PermutationArtifact extends Artifact<PermutationArtifact> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2097933260935878782L;
	private final Set<String> permutationFiles;
	private final String permutationName;
	private final Set<BindingProperty> bindingProperties;

	public PermutationArtifact(Class<? extends Linker> linker, String permutationName, Set<String> permutationFiles, Set<BindingProperty> bindingProperties) {
		super(linker);
		this.permutationName = permutationName;
		this.permutationFiles = permutationFiles;
		this.bindingProperties = bindingProperties;
	}

	@Override
	public int hashCode() {
		return permutationFiles.hashCode();
	}

	@Override
	protected int compareToComparableArtifact(PermutationArtifact o) {
		return permutationName.compareTo(o.permutationName);
	}

	@Override
	protected Class<PermutationArtifact> getComparableArtifactType() {
		return PermutationArtifact.class;
	}

	public Set<String> getPermutationFiles() {
		return permutationFiles;
	}

	public String getPermutationName() {
		return permutationName;
	}

	public Set<BindingProperty> getBindingProperties() {
		return bindingProperties;
	}

}
