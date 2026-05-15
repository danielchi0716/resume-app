import SwiftUI
import Shared

struct ContactButton: View {
    let contact: Contact
    @Environment(\.hig) private var hig
    @Environment(\.higScheme) private var scheme

    var body: some View {
        let url = (contact.url as? String).flatMap { URL(string: $0) }
        Group {
            if let url {
                Link(destination: url) { content }
                    .buttonStyle(.plain)
            } else {
                content
            }
        }
    }

    private var content: some View {
        VStack(spacing: 6) {
            ZStack {
                RoundedRectangle(cornerRadius: 18, style: .continuous)
                    .fill(hig.secondarySystemGroupedBackground)
                Image(systemName: iconName)
                    .font(.system(size: 26, weight: .medium))
                    .foregroundColor(iconColor)
            }
            .frame(width: 56, height: 56)
            .shadow(color: scheme == .dark ? .clear : .black.opacity(0.04),
                    radius: 1, y: 1)
            labelText
                .font(.system(size: 12, weight: .medium))
                .foregroundColor(hig.secondaryLabel)
        }
    }

    private var iconName: String {
        switch contact.type {
        case .email:    return "envelope.fill"
        case .phone:    return "phone.fill"
        case .github:   return "chevron.left.forwardslash.chevron.right"
        case .linkedin: return "briefcase"
        default:        return "link"
        }
    }

    private var iconColor: Color {
        switch contact.type {
        case .email:    return hig.blue
        case .phone:    return hig.green
        case .linkedin: return hig.indigo
        default:        return hig.label
        }
    }

    private var labelText: Text {
        switch contact.type {
        case .email:    return Text("contact.email")
        case .phone:    return Text("contact.phone")
        case .github:   return Text("contact.github")
        case .linkedin: return Text("contact.linkedin")
        default:        return Text(verbatim: contact.value)
        }
    }
}
