export default class ChatPopup extends HTMLElement {
    constructor(parent) {
        super();

        this.id = 'chat-popup';

        this.overlay = document.createElement('div');
        this.overlay.style.position = 'fixed';
        this.overlay.style.top = '0';
        this.overlay.style.left = '0';
        this.overlay.style.width = '100vw';
        this.overlay.style.height = '100vh';
        this.overlay.style.backgroundColor = 'rgba(0, 0, 0, 0.5)';
        this.overlay.style.display = 'flex';
        this.overlay.style.justifyContent = 'center';
        this.overlay.style.alignItems = 'center';
        this.overlay.style.zIndex = '999';
        this.overlay.style.visibility = 'hidden';

        this.popup = document.createElement('div');
        this.popup.style.backgroundColor = '#fff';
        this.popup.style.padding = '20px';
        this.popup.style.borderRadius = '8px';
        this.popup.style.boxShadow = '0 2px 10px rgba(0,0,0,0.3)';
        this.popup.style.minWidth = '300px';
        this.popup.style.maxWidth = '90vw';
        this.popup.style.display = 'flex';
        this.popup.style.flexDirection = 'column';
        this.popup.style.alignItems = 'flex-end';

        this.header = document.createElement('h4');
        this.header.style.alignSelf = 'center';
        this.header.style.fontWeight = 'normal';
        
        this.closeBtn = document.createElement('button');
        this.closeBtn.textContent = 'Ok';
        this.closeBtn.addEventListener('click', () => {
            this.setVisibility(false);
        });

        this.popup.appendChild(this.header);
        this.popup.appendChild(this.closeBtn);
        this.overlay.appendChild(this.popup);

        this.appendChild(this.overlay);

        this.parent = parent;
        this.parent.appendChild(this);
    }

    connectedCallback() {
        this.updateTitle();
    }

    static get observedAttributes() {
        return ['title'];
    }

    attributeChangedCallback(name, oldValue, newValue) {
        if (name === 'title') {
            this.updateTitle();
        }
    }

    updateTitle() {
        this.header.textContent = this.getAttribute('title') || 'Chat';
    }

    setVisibility(visible) {
        this.overlay.style.visibility = visible ? 'visible' : 'hidden';
    }
}

customElements.define('chat-popup', ChatPopup);