export default class PanelButton extends HTMLElement {
    constructor(parent, sidePanel) {
        super();

		if (!(parent instanceof HTMLElement)) {
			throw new Error('Parent must be valid HTMLElement instances');
		}
		
        this.style.display = 'flex';
		
		this.sidePanel = sidePanel;

        this.button = document.createElement('button');
        this.button.type = 'submit';
        this.button.className = 'side-panel-button';
		this.button.addEventListener('click', () => this.sidePanel.toggleSidePanel());
        
        this.appendChild(this.button);
        this.state = 'normal';
        this.setButtonState(this.state);

        this.parent = parent;
        this.parent.appendChild(this);
    }

    setButtonState(state) {
        switch(state) {
            case 'normal':
                this.button.innerHTML = `
					<svg fill="#ffffff" version="1.1" id="Capa_1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" viewBox="0 0 24.75 24.75" xml:space="preserve">
						<g id="SVGRepo_bgCarrier" stroke-width="0"></g>
						<g id="SVGRepo_tracerCarrier" stroke-linecap="round" stroke-linejoin="round"></g>
						<g id="SVGRepo_iconCarrier"> 
							<g> 
								<path d="M0,3.875c0-1.104,0.896-2,2-2h20.75c1.104,0,2,0.896,2,2s-0.896,2-2,2H2C0.896,5.875,0,4.979,0,3.875z M22.75,10.375H2 c-1.104,0-2,0.896-2,2c0,1.104,0.896,2,2,2h20.75c1.104,0,2-0.896,2-2C24.75,11.271,23.855,10.375,22.75,10.375z M22.75,18.875H2 c-1.104,0-2,0.896-2,2s0.896,2,2,2h20.75c1.104,0,2-0.896,2-2S23.855,18.875,22.75,18.875z"></path> 
							</g> 
						</g>
					</svg>`;
                this.state = state;
                break;
            default:
                console.error("Not a valid state: ", state);
                break;
        }
    }
}

customElements.define('panel-button', PanelButton);