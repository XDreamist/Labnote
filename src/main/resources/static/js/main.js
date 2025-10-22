import ChatButton from "./chat/ChatButton.js";
import ChatMessage from "./chat/ChatMessage.js";
import ChatPopup from "./chat/ChatPopup.js";
import PanelButton from "./panel/PanelButton.js";
import SidePanel from "./panel/SidePanel.js";


const API_BASE = "http://localhost:8086/api/";
const SEARCH_EP = `${API_BASE}search`;
const STOP_EP   = `${API_BASE}stop`;
const DOCS_EP   = `${API_BASE}titles`;

const chatHeader = document.getElementById('chatHeader');
const chatMessages = document.getElementById('chatMessages');
const chatForm = document.getElementById('chatForm');
const userInput = document.getElementById('userInput');
const sidePanelContainer = document.getElementById('side-panel');

const sidePanel = new SidePanel(sidePanelContainer, DOCS_EP);
sidePanel.loadDocuments();

const panelButton = new PanelButton(chatHeader, sidePanel);
const chatButton = new ChatButton(chatForm);

const chatPopup = new ChatPopup(document.body);
chatPopup.setAttribute('title', 'You need to select atleast one document to use it!');

let controller = null;


function createMessage(position, text) {
    const msg = new ChatMessage(chatMessages, position, text);
    chatMessages.scrollTop = chatMessages.scrollHeight;
    return msg;
}


chatForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    if (chatButton.state === 'stop') {
        if (controller) controller.abort();

        try {
            await fetch(STOP_EP, { method: 'POST' });
            console.log('Stop signal sent');
        } catch (err) {
            console.error('Failed to send stop: ', err);
        }

        userInput.disabled = false;
        chatButton.setButtonState('send');
        return;
    }

    const question = userInput.value.trim();
    if (!question) return;

    const docsToSearch = sidePanel.getSelectedDocumentIds();
    if (docsToSearch.length <= 0) {
        // alert("You need to select atleast one document to use it!");
        chatPopup.setVisibility(true);
        return;
    }

    createMessage('right', question);
    userInput.value = '';
    userInput.disabled = true;
    chatButton.setButtonState('stop');

    controller = new AbortController();

    // Create a single loading message for the bot response
    const botMessage = createMessage('left', '...');
    botMessage.removeTimestamp();

    try {
        const response = await fetch(SEARCH_EP, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ prompt: question, documentIds: docsToSearch }),
            signal: controller.signal,
        });

        if (!response.ok) throw new Error(`Network error: ${response.status}`);

        const reader = response.body.getReader();
        const decoder = new TextDecoder();
		botMessage.clearMessage();
		
        let done = false;
		
        while (true) {
            const { value, done: doneReading } = await reader.read();
            console.log("The read result: ", value);
            done = doneReading;
			
            if (value) {
                const chunk = decoder.decode(value, { stream: !done });
				console.log('Received chunk: ', chunk);
                botMessage.appendMessageChunk(chunk);
                // chatMessages.scrollTop = chatMessages.scrollHeight; // Use this if you want frequent scroll down
            }

            if (done) break;
        }

        botMessage.appendTimestamp();
    } catch (error) {
        if (error.name === 'AbortError') {
            botMessage.appendText('\n\nGeneration stopped!');
            botMessage.appendTimestamp();
        }
        else {
            botMessage.destroy();
            createMessage('left', 'Oops! Something went wrong. Please try again.');
            console.error(error);
        }
    } finally {
        userInput.disabled = false;
        chatButton.setButtonState('send');
        controller = null;
    }
});