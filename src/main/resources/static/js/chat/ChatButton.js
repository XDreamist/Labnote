export default class ChatButton extends HTMLElement {
    constructor(parent) {
        super();

        this.style.display = 'flex';

        this.button = document.createElement('button');
        this.button.type = 'submit';
        this.button.className = 'chat-button';
        
        this.appendChild(this.button);
        this.state = 'send';
        this.setButtonState(this.state);

        this.parent = parent;
        this.parent.appendChild(this);
    }

    onClick(callback) {
        this.button.addEventListener('click', callback);
    }

    setButtonState(state) {
        switch(state) {
            case 'send':
                this.button.innerHTML = `
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
                        <path d="M2 21L23 12L2 3V10L17 12L2 14V21Z"/>
                    </svg>`;
                this.state = state;
                break;
            case 'stop':
                this.button.innerHTML = `
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
                    <rect x="4" y="4" width="16" height="16"/>
                    </svg>`;
                this.state = state;
                break;
            default:
                console.error("Not a valid state: ", state);
                break;
        }
    }
}

customElements.define('chat-button', ChatButton);