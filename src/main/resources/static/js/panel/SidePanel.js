import { fetchJSON, createElement } from '../utils.js';

export default class container {
    constructor(containerElement, docsAPI) {
        this.container = containerElement;
        this.docsAPI = docsAPI;
		
        this.documentList = createElement('div', { id: 'documentList' });
		this.containerHidden = true;
		
		this.closeButton = createElement('button', { 
		    id: 'containerCloseButton',
		    className: 'close-button',
		    'aria-label': this.containerHidden ? 'Open document panel' : 'Close document panel',
		    style: 'background-color: transparent; color: #343541; border: none; border-radius: 18px; padding: 6px 8px; padding-bottom: 2px; cursor: pointer; font-size: 14px; margin-left: 10px; margin-top: 0;'
		});
		this.closeButton.innerHTML = `
		    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
		        <path d="M6 6L18 18M18 6L6 18" stroke="#343541" stroke-width="3" stroke-linecap="round"/>
		    </svg>
		`;
		this.closeButton.addEventListener('click', () => {
		    this.toggleSidePanel();
		    this.closeButton.setAttribute('aria-label', this.containerHidden ? 'Open document panel' : 'Close document panel');
		});

		// Create a header container for the heading and close button
		const headerContainer = createElement('div', {
		    style: 'display: flex; align-items: center; justify-content: space-between; padding: 10px;'
		});
		const heading = createElement('h3', { style: 'margin: 0;' }, 'Documents');
		headerContainer.appendChild(heading);
		headerContainer.appendChild(this.closeButton);

		this.container.appendChild(headerContainer);
        this.container.appendChild(this.documentList);
    }
	
	toggleSidePanel() {
	    if (this.containerHidden) {
	        // Open animation
	        this.container.style.display = 'block';
	        // Force reflow to ensure animation plays
	        this.container.offsetWidth;
			if (window.matchMedia("(max-width: 480px)").matches) {
			    this.container.style.width = '220px';
			} else if (window.matchMedia("(max-width: 960px)").matches) {
			    this.container.style.width = '240px';
			} else {
			    this.container.style.width = '260px';
			}
	        this.container.style.opacity = '1';
	    } else {
	        // Close animation
	        this.container.style.width = '0px';
	        this.container.style.opacity = '0';
	        // Wait for animation to complete before hiding
	        this.container.addEventListener('transitionend', () => {
	            if (this.containerHidden) {
	                this.container.style.display = 'none';
	            }
	        }, { once: true });
	    }
	    this.containerHidden = !this.containerHidden;
	    //this.setButtonState(this.containerHidden ? 'normal' : 'active');
	    /*this.dispatchEvent(new CustomEvent('panel-toggle', {
	        detail: { isHidden: this.containerHidden },
	        bubbles: true,
	        composed: true
	    }));*/
	}

    async loadDocuments() {
        try {
            const docs = await fetchJSON(this.docsAPI);
            this.documentList.innerHTML = '';

            const fragment = document.createDocumentFragment();

            Object.entries(docs).forEach(([index, docTitle]) => {
                const checkbox = createElement('input', { type: 'checkbox', value: docTitle.id, checked: true });
                const label = createElement('label', {}, checkbox, docTitle.text);
                fragment.appendChild(label);
                fragment.appendChild(document.createElement('br'));
            });

            this.documentList.appendChild(fragment);
        } catch (error) {
            console.error('Failed to load documents:', error);
            this.documentList.textContent = 'Failed to load documents';
        }
    }

    getSelectedDocumentIds() {
        return Array.from(this.documentList.querySelectorAll('input[type="checkbox"]:checked'))
            .map(checkbox => checkbox.value);
    }
}
