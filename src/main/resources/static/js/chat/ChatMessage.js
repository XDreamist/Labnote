import { getCurrentTime } from '../utils.js';

export default class ChatMessage extends HTMLElement {
    constructor(parent, position, message) {
        super();

        this.classList.add('message', position);
        this.parent = parent;

        this.timestampSpan = document.createElement('span');
        this.timestampSpan.className = 'timestamp';

		this.isThinking = false;
		this.thinkSpan = null;
		
        this.setMessage(message);
        this.appendChild(this.timestampSpan);

        this.parent.appendChild(this);
    }

    setMessage(message) {
        this.textContent = '';
        this.appendChild(this.timestampSpan);
        this.appendText(message);
        this.updateTimestamp();
    }

    appendText(text) {
        const textNode = document.createTextNode(text);
        this.insertBefore(textNode, this.timestampSpan);
    }

    updateTimestamp() {
        this.timestampSpan.textContent = getCurrentTime();
    }

    removeTimestamp() {
        this.timestampSpan.textContent = '';
    }

    appendTimestamp() {
        if (!this.contains(this.timestampSpan)) {
            this.appendChild(this.timestampSpan);
        }
        this.updateTimestamp();
    }

    clearMessage() {
        this.textContent = '';
        this.appendChild(this.timestampSpan);
		
		this.isThinking = false;
		this.thinkSpan = null;
    }

    appendMessageChunk(chunk) {
		if (chunk === '<think>' && !think.isThinking) {
			this.isThinking = true;
			this.thinkSpan = document.createElement('span');
			this.thinkSpan.className = 'think-content';
			this.insertBefore(this.thinkSpan, this.timestampSpan);
			return;
		}
		else if (this.isThinking && chunk === '</think>') {
			this.isThinking = false;
			this.thinkSpan = null;
			return;
		}
		else if (this.isThinking) {
			const textNode = document.createTextNode(chunk);
			this.thinkSpan.appendChild(textNode);
		}
		else {
			const textNode = document.createTextNode(chunk);
			this.insertBefore(textNode, this.timestampSpan);	
		}
    }

    destroy() {
        this.parent.remove(this);
    }
}

customElements.define('chat-message', ChatMessage);
