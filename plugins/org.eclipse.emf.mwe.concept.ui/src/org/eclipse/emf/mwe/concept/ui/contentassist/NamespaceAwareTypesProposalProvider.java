package org.eclipse.emf.mwe.concept.ui.contentassist;

import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.mwe.concept.ui.scoping.NamespaceAwareScopeProvider;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.xtext.common.types.xtext.ui.JdtTypesProposalProvider;
import org.eclipse.xtext.scoping.impl.ImportedNamespaceAwareLocalScopeProvider.ImportNormalizer;
import org.eclipse.xtext.ui.common.editor.contentassist.ICompletionProposalFactory;
import org.eclipse.xtext.ui.core.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.ui.core.editor.contentassist.ICompletionProposalAcceptor;

import com.google.inject.Inject;

public class NamespaceAwareTypesProposalProvider extends JdtTypesProposalProvider {

	@Inject
	private NamespaceAwareScopeProvider nameSpaceAwareScopeProvider;
	
	@Override
	protected void createTypeProposal(String typeName, ICompletionProposalFactory proposalFactory, 
			ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		if (acceptor.canAcceptMoreProposals() && !"java.lang.Object".equals(typeName)) {
			int lastDot = typeName.lastIndexOf('.');
			String displayString = typeName;
			if (lastDot != -1)
				displayString = typeName.substring(lastDot + 1) + " - " + typeName.substring(0, lastDot);
			ICompletionProposal proposal = proposalFactory.createCompletionProposal(
					toShortName(context.getRootModel(), typeName), displayString, null, context);
			acceptor.accept(proposal);
		}
	}
	
	public String toShortName(EObject context, String typeName) {
		Set<ImportNormalizer> importNormalizer = nameSpaceAwareScopeProvider.getImportNormalizer(context);
		String result = typeName;
		for(ImportNormalizer normalizer: importNormalizer) {
			String candidate = normalizer.longToShortName(typeName);
			if (candidate != null && candidate.length() < result.length())
				result = candidate;
		}
		return result;
	}
	
}