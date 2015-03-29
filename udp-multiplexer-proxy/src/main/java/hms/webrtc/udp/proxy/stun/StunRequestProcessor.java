package hms.webrtc.udp.proxy.stun;

import org.mobicents.media.io.stun.StunException;
import org.mobicents.media.io.stun.messages.StunMessageFactory;
import org.mobicents.media.io.stun.messages.StunRequest;
import org.mobicents.media.io.stun.messages.StunResponse;
import org.mobicents.media.io.stun.messages.attributes.StunAttribute;
import org.mobicents.media.io.stun.messages.attributes.StunAttributeFactory;
import org.mobicents.media.io.stun.messages.attributes.general.PriorityAttribute;
import org.mobicents.media.io.stun.messages.attributes.general.UsernameAttribute;
import org.mobicents.media.server.io.network.TransportAddress;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by isuru on 3/29/15.
 */
public class StunRequestProcessor {

    public static byte[] processRequest(StunRequest request, InetSocketAddress localPeer, InetSocketAddress remotePeer) throws IOException {

        UsernameAttribute remoteUnameAttribute = (UsernameAttribute) request.getAttribute(StunAttribute.USERNAME);
        String remoteUsername = new String(remoteUnameAttribute.getUsername());

		/*
		 * The username for the credential is formed by concatenating the
		 * username fragment provided by the peer with the username fragment of
		 * the agent sending the request, separated by a colon (":").
		 */
        int colon = remoteUsername.indexOf(":");
        String localUFrag = remoteUsername.substring(0, colon);
        String remoteUfrag = remoteUsername.substring(colon);

		/*
		 * An agent MUST include the PRIORITY attribute in its Binding request.
		 * This priority value will be computed identically to how the priority
		 * for the local candidate of the pair was computed, except that the
		 * type preference is set to the value for peer reflexive candidate
		 * types
		 */
        long priority = extractPriority(request);

        // Produce Binding Response
        TransportAddress transportAddress = new TransportAddress(remotePeer.getAddress(), remotePeer.getPort(), TransportAddress.TransportProtocol.UDP);
        StunResponse response = StunMessageFactory.createBindingResponse(request, transportAddress);
        byte[] transactionID = request.getTransactionId();
        try {
            response.setTransactionID(transactionID);
        } catch (StunException e) {
            throw new IOException("Illegal STUN Transaction ID: " + new String(transactionID), e);
        }

		/*
		 * Add USERNAME and MESSAGE-INTEGRITY attribute in the response. The
		 * responses utilize the same usernames and passwords as the requests
		 */
        String localUsername = remoteUfrag.concat(":").concat(localUFrag);
        StunAttribute unameAttribute = StunAttributeFactory.createUsernameAttribute(localUsername);
        response.addAttribute(unameAttribute);

        /*byte[] localKey = this.iceAuthenticator.getLocalKey(localUFrag);
        MessageIntegrityAttribute messageIntegrityAttribute = StunAttributeFactory.createMessageIntegrityAttribute(remoteUsername, localKey);
        response.addAttribute(messageIntegrityAttribute);
*/
        // Pass response to the server
        return response.encode();
    }

    private static long extractPriority(StunRequest request) throws IllegalArgumentException {
        PriorityAttribute priorityAttr = (PriorityAttribute) request.getAttribute(StunAttribute.PRIORITY);
        if (priorityAttr == null) {
            throw new IllegalArgumentException("Missing PRIORITY attribute!");
        }
        return priorityAttr.getPriority();
    }
}
