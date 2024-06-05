use std::fmt;

#[derive(Debug, PartialEq)]
pub enum Node<'a> {
    Bracket {
        expr: &'a Node<'a>,
    },
    Plus {
        lhs: &'a Node<'a>,
        rhs: &'a Node<'a>,
    },
    Minus {
        lhs: &'a Node<'a>,
        rhs: &'a Node<'a>,
    },
    Multiply {
        lhs: &'a Node<'a>,
        rhs: &'a Node<'a>,
    },
    Divide {
        lhs: &'a Node<'a>,
        rhs: &'a Node<'a>,
    },
    Power {
        lhs: &'a Node<'a>,
        rhs: &'a Node<'a>,
    },
    Constant {
        value: i64,
    },
    Variable {
        name: String,
    },
    Function {
        name: String,
        expr: &'a Node<'a>,
    },
}

impl fmt::Display for Node<'_> {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        match self {
            Node::Bracket { expr } => write!(f, "({})", expr),
            Node::Plus { lhs, rhs } => write!(f, "{}+{}", lhs, rhs),
            Node::Minus { lhs, rhs } => write!(f, "{}-{}", lhs, rhs),
            Node::Multiply { lhs, rhs } => write!(f, "{}*{}", lhs, rhs),
            Node::Divide { lhs, rhs } => write!(f, "{}/{}", lhs, rhs),
            Node::Power { lhs, rhs } => write!(f, "{}^{}", lhs, rhs),
            Node::Constant { value } => write!(f, "{}", value),
            Node::Variable { name } => write!(f, "{}", name),
            Node::Function { name, expr } => write!(f, "{}({})", name, expr),
        }
    }
}
