import type { Meta, StoryObj } from "@storybook/react";
import { Input } from "./input";
import { User, Mail, Lock, Phone, Search, Calendar } from "lucide-react";

const meta = {
    title: "UI/Input",
    component: Input,
    parameters: {
        layout: "centered",
    },
    tags: ["autodocs"],
    argTypes: {
        label: {
            control: "text",
            description: "Label text for the input field",
        },
        type: {
            control: "select",
            options: [
                "text",
                "email",
                "password",
                "tel",
                "search",
                "date",
                "number",
            ],
            description: "HTML input type",
        },
        placeholder: {
            control: "text",
            description: "Placeholder text",
        },
        disabled: {
            control: "boolean",
            description: "Disables the input",
        },
        required: {
            control: "boolean",
            description: "Makes the input required",
        },
    },
} satisfies Meta<typeof Input>;

export default meta;
type Story = StoryObj<typeof meta>;

// Default story
export const Default: Story = {
    args: {
        label: "Username",
        icon: User,
        placeholder: "Enter your username",
        id: "username",
    },
};

// Email input
export const Email: Story = {
    args: {
        label: "Email Address",
        icon: Mail,
        type: "email",
        placeholder: "you@example.com",
        id: "email",
    },
};

// Password input
export const Password: Story = {
    args: {
        label: "Password",
        icon: Lock,
        type: "password",
        placeholder: "Enter your password",
        id: "password",
    },
};

// Phone input
export const PhoneInput: Story = {
    args: {
        label: "Phone Number",
        icon: Phone,
        type: "tel",
        placeholder: "+1 (555) 000-0000",
        id: "phone",
    },
};

export const SearchField: Story = {
    args: {
        label: "Search",
        icon: Search,
        type: "search",
        placeholder: "Search for books...",
        id: "search",
    },
};

export const DatePicker: Story = {
    args: {
        label: "Date of Birth",
        icon: Calendar,
        type: "date",
        id: "dob",
    },
};

export const Disabled: Story = {
    args: {
        label: "Username",
        icon: User,
        placeholder: "This field is disabled",
        id: "username-disabled",
        disabled: true,
    },
};

export const Required: Story = {
    args: {
        label: "Email Address",
        icon: Mail,
        type: "email",
        placeholder: "you@example.com",
        id: "email-required",
        required: true,
    },
};

export const WithValue: Story = {
    args: {
        label: "Username",
        icon: User,
        placeholder: "Enter your username",
        id: "username-value",
        defaultValue: "johndoe",
    },
};
